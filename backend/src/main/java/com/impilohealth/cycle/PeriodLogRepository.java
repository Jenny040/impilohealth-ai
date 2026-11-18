package com.impilohealth.cycle;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodLogRepository extends JpaRepository<PeriodLog, Long> {

    List<PeriodLog> findByUserIdOrderByStartDateDesc(Long userId);

    /** Latest 4 periods = up to 3 cycle lengths for the rolling average. */
    List<PeriodLog> findTop4ByUserIdOrderByStartDateDesc(Long userId);

    boolean existsByUserIdAndStartDate(Long userId, LocalDate startDate);
}