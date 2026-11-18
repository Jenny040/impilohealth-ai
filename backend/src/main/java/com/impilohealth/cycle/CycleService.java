package com.impilohealth.cycle;

import com.impilohealth.audit.AuditService;
import com.impilohealth.common.exception.BadRequestException;
import com.impilohealth.common.exception.ConflictException;
import com.impilohealth.cycle.CycleDtos.PeriodRequest;
import com.impilohealth.cycle.CycleDtos.PeriodResponse;
import com.impilohealth.cycle.CycleDtos.SymptomRequest;
import com.impilohealth.cycle.CycleDtos.SymptomResponse;
import com.impilohealth.user.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CycleService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Johannesburg");

    private final UserRepository users;
    private final PeriodLogRepository periods;
    private final SymptomLogRepository symptoms;
    private final AuditService audit;

    public CycleService(UserRepository users, PeriodLogRepository periods,
                        SymptomLogRepository symptoms, AuditService audit) {
        this.users = users;
        this.periods = periods;
        this.symptoms = symptoms;
        this.audit = audit;
    }

    private Long userId(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow().getId();
    }

    @Transactional
    public PeriodResponse logPeriod(String email, PeriodRequest req) {
        if (req.endDate() != null && req.endDate().isBefore(req.startDate())) {
            throw new BadRequestException("endDate must not be before startDate");
        }
        Long uid = userId(email);
        if (periods.existsByUserIdAndStartDate(uid, req.startDate())) {
            throw new ConflictException("A period starting on this date is already logged");
        }
        PeriodLog saved = periods.save(new PeriodLog(uid, req.startDate(), req.endDate(), req.flow()));
        audit.record(email, "CREATE", "period_logs");
        return PeriodResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<PeriodResponse> listPeriods(String email) {
        audit.record(email, "READ", "period_logs");
        return periods.findByUserIdOrderByStartDateDesc(userId(email)).stream().map(PeriodResponse::from).toList();
    }

    @Transactional
    public SymptomResponse logSymptoms(String email, SymptomRequest req) {
        Long uid = userId(email);
        SymptomLog log = symptoms.findByUserIdAndLogDate(uid, req.date())
                .orElseGet(() -> new SymptomLog(uid, req.date()));
        log.update(req.flow(), req.symptoms());
        SymptomLog saved = symptoms.save(log);
        audit.record(email, "CREATE", "symptom_logs");
        return SymptomResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<SymptomResponse> listSymptoms(String email, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new BadRequestException("'to' must not be before 'from'");
        }
        audit.record(email, "READ", "symptom_logs");
        return symptoms.findByUserIdAndLogDateBetweenOrderByLogDate(userId(email), from, to)
                .stream().map(SymptomResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CyclePredictor.Prediction prediction(String email) {
        List<LocalDate> starts = periods.findTop4ByUserIdOrderByStartDateDesc(userId(email))
                .stream().map(PeriodLog::getStartDate).toList();
        audit.record(email, "READ", "cycle_prediction");
        return CyclePredictor.predict(starts, LocalDate.now(ZONE));
    }
}