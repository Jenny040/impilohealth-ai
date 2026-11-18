package com.impilohealth.cycle;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

public final class CycleDtos {

    private CycleDtos() {
    }

    public record PeriodRequest(
            @NotNull @PastOrPresent LocalDate startDate,
            @PastOrPresent LocalDate endDate,
            FlowIntensity flow) {
    }

    public record PeriodResponse(Long id, LocalDate startDate, LocalDate endDate, FlowIntensity flow) {
        static PeriodResponse from(PeriodLog p) {
            return new PeriodResponse(p.getId(), p.getStartDate(), p.getEndDate(), p.getFlow());
        }
    }

    public record SymptomRequest(
            @NotNull @PastOrPresent LocalDate date,
            FlowIntensity flow,
            Set<Symptom> symptoms) {
    }

    public record SymptomResponse(LocalDate date, FlowIntensity flow, Set<Symptom> symptoms) {
        static SymptomResponse from(SymptomLog s) {
            return new SymptomResponse(s.getLogDate(), s.getFlow(), Set.copyOf(s.getSymptoms()));
        }
    }
}