package com.impilohealth.cycle;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

/** One period (start date, optional end date). Cycle length = gap between consecutive start dates. */
@Entity
@Table(name = "period_logs",
        uniqueConstraints = @UniqueConstraint(name = "uq_period_user_start", columnNames = {"userId", "startDate"}))
public class PeriodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private FlowIntensity flow;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected PeriodLog() {
    }

    public PeriodLog(Long userId, LocalDate startDate, LocalDate endDate, FlowIntensity flow) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flow = flow;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public FlowIntensity getFlow() { return flow; }
}