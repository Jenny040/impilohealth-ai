package com.impilohealth.audit;

import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    /** action examples: READ, CREATE, UPDATE, DELETE, EXPORT, AI_CHAT. */
    public void record(String actor, String action, String resource) {
        repo.save(new AuditLog(actor, action, resource));
    }
}
