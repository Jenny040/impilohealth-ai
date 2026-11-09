package com.impilohealth.audit;

import jakarta.persistence.*;
import java.time.Instant;

/** POPIA audit trail: who accessed / changed / exported what, and when (12-month retention). */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false, length = 32)
    private String action;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditLog() {
    }

    public AuditLog(String actor, String action, String resource) {
        this.actor = actor;
        this.action = action;
        this.resource = resource;
    }

    public Long getId() { return id; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public Instant getCreatedAt() { return createdAt; }
}
