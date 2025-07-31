package com.cooperfilme.controller;

import com.cooperfilme.entity.Script;
import com.cooperfilme.entity.User;
import com.cooperfilme.service.ScriptService;
import com.cooperfilme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private UserService userService;

    // Rota pública para envio de roteiro
    @PostMapping("/submit")
    public ResponseEntity<Script> submitScript(@RequestBody Script script) {
        script.setStatus(Script.Status.AGUARDANDO_ANALISE);
        script.setSubmissionDate(LocalDateTime.now());
        Script savedScript = scriptService.save(script);
        return ResponseEntity.ok(savedScript);
    }

    // Rota pública para consulta de status
    @GetMapping("/status/{id}")
    public ResponseEntity<Script.Status> getScriptStatus(@PathVariable Long id) {
        Optional<Script> script = scriptService.findById(id);
        if (script.isPresent()) {
            return ResponseEntity.ok(script.get().getStatus());
        }
        return ResponseEntity.notFound().build();
    }

    // Rotas protegidas
    @GetMapping
    public ResponseEntity<List<Script>> getAllScripts(
            @RequestParam(required = false) Script.Status status,
            @RequestParam(required = false) String clientEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
    	List<Script> scripts = new ArrayList<>();
    	
    	if (status == null && clientEmail == null && startDate == null && endDate == null) {
    	    scripts = scriptService.findAll();
    	    return ResponseEntity.ok(scripts);
    	}
    	
        scripts = scriptService.findByFilters(status, clientEmail, startDate, endDate);
        return ResponseEntity.ok(scripts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Script> getScript(@PathVariable Long id) {
        Optional<Script> script = scriptService.findById(id);
        if (script.isPresent()) {
            return ResponseEntity.ok(script.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Script> changeStatus(@PathVariable Long id, 
                                              @RequestParam Script.Status newStatus,
                                              @RequestParam(required = false) String justification) {
        try {
            User currentUser = getCurrentUser();
            Script updatedScript = scriptService.changeStatus(id, newStatus, currentUser, justification);
            return ResponseEntity.ok(updatedScript);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<Script> vote(@PathVariable Long id,
                                      @RequestParam Boolean approved,
                                      @RequestParam(required = false) String comments) {
        try {
            User currentUser = getCurrentUser();
            Script updatedScript = scriptService.vote(id, currentUser, approved, comments);
            return ResponseEntity.ok(updatedScript);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}

