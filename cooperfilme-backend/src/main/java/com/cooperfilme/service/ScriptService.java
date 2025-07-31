package com.cooperfilme.service;

import com.cooperfilme.entity.Script;
import com.cooperfilme.entity.User;
import com.cooperfilme.entity.Vote;
import com.cooperfilme.repository.ScriptRepository;
import com.cooperfilme.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScriptService {

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private VoteRepository voteRepository;

    public Script save(Script script) {
        return scriptRepository.save(script);
    }

    public Optional<Script> findById(Long id) {
        return scriptRepository.findById(id);
    }

    public List<Script> findAll() {
        return scriptRepository.findAll();
    }

    public List<Script> findByFilters(Script.Status status, String clientEmail, 
                                     LocalDateTime startDate, LocalDateTime endDate) {
        return scriptRepository.findByFilters(status, clientEmail, startDate, endDate);
    }

    public Script changeStatus(Long scriptId, Script.Status newStatus, User user, String justification) {
        Optional<Script> scriptOpt = scriptRepository.findById(scriptId);
        if (scriptOpt.isEmpty()) {
            throw new RuntimeException("Roteiro não encontrado");
        }

        Script script = scriptOpt.get();
        
        // Validar transições de status
        if (!isValidStatusTransition(script.getStatus(), newStatus)) {
            throw new RuntimeException("Transição de status inválida");
        }

        // Validar permissões do usuário
        if (!hasPermissionToChangeStatus(script.getStatus(), newStatus, user)) {
            throw new RuntimeException("Usuário não tem permissão para esta transição");
        }

        script.setStatus(newStatus);
        script.setAssignedUser(user);
        
        if (justification != null) {
            if (newStatus == Script.Status.AGUARDANDO_REVISAO || newStatus == Script.Status.RECUSADO) {
                script.setJustification(justification);
            } else if (newStatus == Script.Status.AGUARDANDO_APROVACAO) {
                script.setReviewComments(justification);
            }
        }

        return scriptRepository.save(script);
    }

    public Script vote(Long scriptId, User user, Boolean approved, String comments) {
        Optional<Script> scriptOpt = scriptRepository.findById(scriptId);
        if (scriptOpt.isEmpty()) {
            throw new RuntimeException("Roteiro não encontrado");
        }

        Script script = scriptOpt.get();
        
        if (script.getStatus() != Script.Status.AGUARDANDO_APROVACAO && 
            script.getStatus() != Script.Status.EM_APROVACAO) {
            throw new RuntimeException("Roteiro não está em fase de aprovação");
        }

        if (user.getRole() != User.Role.APROVADOR) {
            throw new RuntimeException("Apenas aprovadores podem votar");
        }

        // Verificar se o usuário já votou
        boolean alreadyVoted = script.getVotes().stream()
                .anyMatch(vote -> vote.getUser().getId().equals(user.getId()));
        
        if (alreadyVoted) {
            throw new RuntimeException("Usuário já votou neste roteiro");
        }

        Vote vote = new Vote();
        vote.setScript(script);
        vote.setUser(user);
        vote.setApproved(approved);
        vote.setComments(comments);
        voteRepository.save(vote);

        // Atualizar status baseado nos votos
        script = scriptRepository.findById(scriptId).get(); // Recarregar para pegar os votos atualizados
        
        long totalVotes = script.getVotes().size();
        long approvedVotes = script.getVotes().stream()
                .mapToLong(v -> v.getApproved() ? 1 : 0)
                .sum();
        long rejectedVotes = totalVotes - approvedVotes;

        if (rejectedVotes > 0) {
            script.setStatus(Script.Status.RECUSADO);
        } else if (totalVotes == 1) {
            script.setStatus(Script.Status.EM_APROVACAO);
        } else if (totalVotes == 2 && approvedVotes == 2 && approved == true) {
            script.setStatus(Script.Status.APROVADO);
        }

        return scriptRepository.save(script);
    }

    private boolean isValidStatusTransition(Script.Status currentStatus, Script.Status newStatus) {
        switch (currentStatus) {
            case AGUARDANDO_ANALISE:
                return newStatus == Script.Status.EM_ANALISE;
            case EM_ANALISE:
                return newStatus == Script.Status.AGUARDANDO_REVISAO || newStatus == Script.Status.RECUSADO;
            case AGUARDANDO_REVISAO:
                return newStatus == Script.Status.EM_REVISAO;
            case EM_REVISAO:
                return newStatus == Script.Status.AGUARDANDO_APROVACAO;
            case AGUARDANDO_APROVACAO:
                return newStatus == Script.Status.EM_APROVACAO || newStatus == Script.Status.RECUSADO;
            case EM_APROVACAO:
                return newStatus == Script.Status.APROVADO || newStatus == Script.Status.RECUSADO;
            case APROVADO:
            case RECUSADO:
                return false; // Estados finais
            default:
                return false;
        }
    }

    private boolean hasPermissionToChangeStatus(Script.Status currentStatus, Script.Status newStatus, User user) {
        switch (currentStatus) {
            case AGUARDANDO_ANALISE:
                return newStatus == Script.Status.EM_ANALISE && user.getRole() == User.Role.ANALISTA;
            case EM_ANALISE:
                return user.getRole() == User.Role.ANALISTA;
            case AGUARDANDO_REVISAO:
                return newStatus == Script.Status.EM_REVISAO && user.getRole() == User.Role.REVISOR;
            case EM_REVISAO:
                return user.getRole() == User.Role.REVISOR;
            default:
                return false;
        }
    }
}

