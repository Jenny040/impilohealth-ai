package com.impilohealth.cycle;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Baseline cycle prediction: rolling average of the last (up to) 3 cycle lengths.
 *
 * This is the rule-based baseline from the roadmap. The Huawei Pangu Prediction model is meant to
 * replace/augment {@link #predict} later; the rest of the backend only depends on {@link Prediction}.
 *
 * Ovulation is estimated 14 days before the next period; the fertile window is ovulation-5 .. ovulation+1.
 * These are estimates, not medical advice or contraception guidance.
 */
public final class CyclePredictor {

    public static final int DEFAULT_CYCLE_DAYS = 28;
    static final int MIN_VALID_CYCLE = 15;
    static final int MAX_VALID_CYCLE = 60;

    private CyclePredictor() {
    }

    public record Prediction(
            boolean available,
            String method,
            int cyclesUsed,
            Integer averageCycleLength,
            LocalDate lastPeriodStart,
            LocalDate nextPeriodStart,
            Long daysUntilNextPeriod,
            LocalDate ovulationDate,
            LocalDate fertileWindowStart,
            LocalDate fertileWindowEnd,
            Long daysUntilFertileWindow) {

        static Prediction none() {
            return new Prediction(false, "insufficient-data", 0, null, null, null, null, null, null, null, null);
        }
    }

    /** @param periodStarts start dates of logged periods, any order */
    public static Prediction predict(List<LocalDate> periodStarts, LocalDate today) {
        if (periodStarts == null || periodStarts.isEmpty()) {
            return Prediction.none();
        }
        List<LocalDate> sorted = new ArrayList<>(periodStarts);
        sorted.sort(Comparator.naturalOrder());
        LocalDate last = sorted.get(sorted.size() - 1);

        // Up to the last 3 gaps; ignore implausible ones (a missed log makes a gap ~2x too long).
        List<Integer> gaps = new ArrayList<>();
        for (int i = sorted.size() - 1; i > 0 && gaps.size() < 3; i--) {
            long gap = ChronoUnit.DAYS.between(sorted.get(i - 1), sorted.get(i));
            if (gap >= MIN_VALID_CYCLE && gap <= MAX_VALID_CYCLE) {
                gaps.add((int) gap);
            }
        }

        int avg;
        String method;
        if (gaps.isEmpty()) {
            avg = DEFAULT_CYCLE_DAYS;
            method = "default-28-day-cycle";
        } else {
            avg = (int) Math.round(gaps.stream().mapToInt(Integer::intValue).average().orElse(DEFAULT_CYCLE_DAYS));
            method = "rolling-average-last-" + gaps.size() + "-cycles";
        }

        LocalDate next = last.plusDays(avg);
        LocalDate ovulation = next.minusDays(14);
        LocalDate fertileStart = ovulation.minusDays(5);
        LocalDate fertileEnd = ovulation.plusDays(1);

        return new Prediction(true, method, gaps.size(), avg, last, next,
                ChronoUnit.DAYS.between(today, next), ovulation, fertileStart, fertileEnd,
                ChronoUnit.DAYS.between(today, fertileStart));
    }
}