package com.impilohealth.cycle;

import com.impilohealth.cycle.CycleDtos.PeriodRequest;
import com.impilohealth.cycle.CycleDtos.PeriodResponse;
import com.impilohealth.cycle.CycleDtos.SymptomRequest;
import com.impilohealth.cycle.CycleDtos.SymptomResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cycle")
public class CycleController {

    private final CycleService service;

    public CycleController(CycleService service) {
        this.service = service;
    }

    @PostMapping("/periods")
    @ResponseStatus(HttpStatus.CREATED)
    public PeriodResponse logPeriod(@Valid @RequestBody PeriodRequest request, Authentication auth) {
        return service.logPeriod(auth.getName(), request);
    }

    @GetMapping("/periods")
    public List<PeriodResponse> periods(Authentication auth) {
        return service.listPeriods(auth.getName());
    }

    @PostMapping("/symptoms")
    public SymptomResponse logSymptoms(@Valid @RequestBody SymptomRequest request, Authentication auth) {
        return service.logSymptoms(auth.getName(), request);
    }

    /** Dates in ISO format, e.g. ?from=2026-11-01&to=2026-11-30 (for the calendar view). */
    @GetMapping("/symptoms")
    public List<SymptomResponse> symptoms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication auth) {
        return service.listSymptoms(auth.getName(), from, to);
    }

    @GetMapping("/prediction")
    public CyclePredictor.Prediction prediction(Authentication auth) {
        return service.prediction(auth.getName());
    }
}