package com.impilohealth.cycle;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

/** Daily symptom + flow entry. One row per user per day (re-logging replaces it). */
@Entity
@Table(name = "symptom_logs",
        uniqueConstraints = @UniqueConstraint(name = "uq_symptom_user_day", columnNames = {"userId", "logDate"}))
public class SymptomLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    private FlowIntensity flow;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "symptom_log_items", joinColumns = @JoinColumn(name = "symptom_log_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "symptom")
    private Set<Symptom> symptoms = EnumSet.noneOf(Symptom.class);

    protected SymptomLog() {
    }

    public SymptomLog(Long userId, LocalDate logDate) {
        this.userId = userId;
        this.logDate = logDate;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getLogDate() { return logDate; }
    public FlowIntensity getFlow() { return flow; }
    public Set<Symptom> getSymptoms() { return symptoms; }

    public void update(FlowIntensity flow, Set<Symptom> symptoms) {
        this.flow = flow;
        this.symptoms.clear();
        if (symptoms != null) {
            this.symptoms.addAll(symptoms);
        }
    }
}