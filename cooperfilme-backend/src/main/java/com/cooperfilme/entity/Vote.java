package com.cooperfilme.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "script_id")
    private Script script;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private Boolean approved;
    
    private LocalDateTime voteDate = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String comments;
}

