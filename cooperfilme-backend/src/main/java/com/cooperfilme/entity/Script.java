package com.cooperfilme.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "scripts")
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.AGUARDANDO_ANALISE;
    
    private LocalDateTime submissionDate = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;
    
    @Column(columnDefinition = "TEXT")
    private String justification;
    
    @Column(columnDefinition = "TEXT")
    private String reviewComments;
    
    @OneToMany(mappedBy = "script", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;
    
    public enum Status {
        AGUARDANDO_ANALISE,
        EM_ANALISE,
        AGUARDANDO_REVISAO,
        EM_REVISAO,
        AGUARDANDO_APROVACAO,
        EM_APROVACAO,
        APROVADO,
        RECUSADO
    }
}

