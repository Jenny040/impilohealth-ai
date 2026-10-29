package com.impilohealth.ai;

import jakarta.persistence.*;
import java.time.Instant;

/** Stored conversation turns: the memory behind the "Unified Health Profile". */
@Entity
@Table(name = "chat_logs", indexes = @Index(name = "idx_chat_user", columnList = "userId"))
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 16)
    private String role;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ChatLog() {
    }

    public ChatLog(Long userId, String role, String content) {
        this.userId = userId;
        this.role = role;
        this.content = content;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
}
