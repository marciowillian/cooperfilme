package com.cooperfilme.repository;

import com.cooperfilme.entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {
    
    @Query("SELECT s FROM Script s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:clientEmail IS NULL OR s.clientEmail = :clientEmail) AND " +
           "(:startDate IS NULL OR s.submissionDate >= :startDate) AND " +
           "(:endDate IS NULL OR s.submissionDate <= :endDate)")
    List<Script> findByFilters(@Param("status") Script.Status status,
                              @Param("clientEmail") String clientEmail,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);
}

