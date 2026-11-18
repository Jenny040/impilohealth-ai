package com.impilohealth.cycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.impilohealth.cycle.CyclePredictor.Prediction;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CyclePredictorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 9);

    @Test
    void noPeriodsMeansNoPrediction() {
        Prediction p = CyclePredictor.predict(List.of(), TODAY);
        assertFalse(p.available());
        assertEquals("insufficient-data", p.method());
    }

    @Test
    void singlePeriodUsesDefault28Days() {
        Prediction p = CyclePredictor.predict(List.of(LocalDate.of(2026, 11, 1)), TODAY);
        assertTrue(p.available());
        assertEquals("default-28-day-cycle", p.method());
        assertEquals(LocalDate.of(2026, 11, 29), p.nextPeriodStart());
        assertEquals(20, p.daysUntilNextPeriod());
    }

    @Test
    void averagesLastThreeCycles() {
        // gaps: 30, 28, 26 -> avg 28
        List<LocalDate> starts = List.of(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31),
                LocalDate.of(2026, 9, 28), LocalDate.of(2026, 10, 24));
        Prediction p = CyclePredictor.predict(starts, TODAY);
        assertEquals(3, p.cyclesUsed());
        assertEquals(28, p.averageCycleLength());
        assertEquals(LocalDate.of(2026, 11, 21), p.nextPeriodStart());
    }

    @Test
    void inputOrderDoesNotMatter() {
        Prediction a = CyclePredictor.predict(List.of(
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 9, 5)), TODAY);
        Prediction b = CyclePredictor.predict(List.of(
                LocalDate.of(2026, 9, 5), LocalDate.of(2026, 10, 3)), TODAY);
        assertEquals(a, b);
        assertEquals(28, a.averageCycleLength());
    }

    @Test
    void fertileWindowIsFiveDaysBeforeToOneDayAfterOvulation() {
        Prediction p = CyclePredictor.predict(List.of(LocalDate.of(2026, 11, 1)), TODAY);
        // next = Nov 29, ovulation = Nov 15, window Nov 10 .. Nov 16
        assertEquals(LocalDate.of(2026, 11, 15), p.ovulationDate());
        assertEquals(LocalDate.of(2026, 11, 10), p.fertileWindowStart());
        assertEquals(LocalDate.of(2026, 11, 16), p.fertileWindowEnd());
        assertEquals(1, p.daysUntilFertileWindow());
    }

    @Test
    void implausibleGapsFromMissedLogsAreIgnored() {
        // 28-day gap, then a 90-day gap (missed logging) -> only the 28 counts
        List<LocalDate> starts = List.of(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 29), LocalDate.of(2026, 8, 27));
        Prediction p = CyclePredictor.predict(starts, TODAY);
        assertEquals(1, p.cyclesUsed());
        assertEquals(28, p.averageCycleLength());
    }

    @Test
    void latePeriodGivesNegativeDays() {
        Prediction p = CyclePredictor.predict(List.of(LocalDate.of(2026, 10, 1)), TODAY);
        // next = Oct 29, today Nov 9 -> -11
        assertEquals(-11, p.daysUntilNextPeriod());
    }
}