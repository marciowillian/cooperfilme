package com.cooperfilme.service;

import com.cooperfilme.entity.Script;
import com.cooperfilme.entity.User;
import com.cooperfilme.entity.Vote;
import com.cooperfilme.repository.ScriptRepository;
import com.cooperfilme.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptServiceTest {

    @Mock
    private ScriptRepository scriptRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ScriptService scriptService;

    private User analista;
    private User revisor;
    private User aprovador;
    private Script script;

    @BeforeEach
    void setup() {
        analista = new User();
        analista.setId(1L);
        analista.setRole(User.Role.ANALISTA);

        revisor = new User();
        revisor.setId(2L);
        revisor.setRole(User.Role.REVISOR);

        aprovador = new User();
        aprovador.setId(3L);
        aprovador.setRole(User.Role.APROVADOR);

        script = new Script();
        script.setId(100L);
        script.setStatus(Script.Status.AGUARDANDO_ANALISE);
        script.setVotes(new ArrayList<>());
    }

    // Teste para mudança de status válida por analista
    @Test
    void testChangeStatus_ValidTransitionByAnalista() {
        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));
        when(scriptRepository.save(any(Script.class))).thenAnswer(i -> i.getArgument(0));

        Script updated = scriptService.changeStatus(script.getId(), Script.Status.EM_ANALISE, analista, null);

        assertEquals(Script.Status.EM_ANALISE, updated.getStatus());
        assertEquals(analista, updated.getAssignedUser());
        verify(scriptRepository).save(updated);
    }

    // Teste para mudança de status inválida
    @Test
    void testChangeStatus_InvalidTransition_Throws() {
        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            scriptService.changeStatus(script.getId(), Script.Status.AGUARDANDO_REVISAO, analista, null);
        });
        assertEquals("Transição de status inválida", ex.getMessage());
    }

    // Teste para mudança de status sem permissão
    @Test
    void testChangeStatus_NoPermission_Throws() {
        script.setStatus(Script.Status.AGUARDANDO_ANALISE);
        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            scriptService.changeStatus(script.getId(), Script.Status.EM_ANALISE, aprovador, null);
        });
        assertEquals("Usuário não tem permissão para esta transição", ex.getMessage());
    }

    // Teste para votar com aprovador e status correto
    @Test
    void testVote_ValidVote() {
        script.setStatus(Script.Status.AGUARDANDO_APROVACAO);
        script.setVotes(null);
        script.setVotes(new ArrayList<>());

        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));
        when(voteRepository.save(any(Vote.class))).thenAnswer(i -> i.getArgument(0));
        when(scriptRepository.save(any(Script.class))).thenAnswer(i -> i.getArgument(0));

        Script result = scriptService.vote(script.getId(), aprovador, true, "Muito bom");

        assertEquals(Script.Status.AGUARDANDO_APROVACAO, result.getStatus());
        verify(voteRepository).save(any(Vote.class));
        verify(scriptRepository).save(result);
    }

    // Teste para votar com usuário que não é aprovador
    @Test
    void testVote_UserNotAprovador_Throws() {
        script.setStatus(Script.Status.AGUARDANDO_APROVACAO);
        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            scriptService.vote(script.getId(), analista, true, null);
        });
        assertEquals("Apenas aprovadores podem votar", ex.getMessage());
    }

    // Teste para votar fora do status permitido
    @Test
    void testVote_InvalidStatus_Throws() {
        script.setStatus(Script.Status.EM_REVISAO);
        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            scriptService.vote(script.getId(), aprovador, true, null);
        });
        assertEquals("Roteiro não está em fase de aprovação", ex.getMessage());
    }

    // Teste para votar duas vezes pelo mesmo usuário
    @Test
    void testVote_AlreadyVoted_Throws() {
        script.setStatus(Script.Status.AGUARDANDO_APROVACAO);
        Vote vote = new Vote();
        vote.setUser(aprovador);
        script.setVotes(Arrays.asList(vote));

        when(scriptRepository.findById(script.getId())).thenReturn(Optional.of(script));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            scriptService.vote(script.getId(), aprovador, true, null);
        });
        assertEquals("Usuário já votou neste roteiro", ex.getMessage());
    }
}
