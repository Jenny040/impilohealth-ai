package com.impilohealth.cycle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymptomLogRepository extends JpaRepository<SymptomLog, Long> {

    Optional<SymptomLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    List<SymptomLog> findByUserIdAndLogDateBetweenOrderByLogDate(Long userId, LocalDate from, LocalDate to);
}