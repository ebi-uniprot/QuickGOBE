package uk.ac.ebi.quickgo.annotation.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link StatisticsValue} class.
 */
class StatisticsValueTest {

    @Test
    void nullIdThrowsException() {
        String key = null;
        long total = 2;
        long occurrence = 1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsValue(key, occurrence, total));
        assertTrue(exception.getMessage().contains("Stats key cannot be null or empty"));
    }

    @Test
    void emptyIdThrowsException() {
        String key = "";
        long total = 2;
        long occurrence = 1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsValue(key, occurrence, total));
        assertTrue(exception.getMessage().contains("Stats key cannot be null or empty"));
    }

    @Test
    void negativeOccurrenceThrowsException() {
        String key = "key";
        long total = 2;
        long occurrence = -1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsValue(key, occurrence, total));
        assertTrue(exception.getMessage().contains("Stats hits cannot be a negative value: " + occurrence));
    }

    @Test
    void negativeTotalThrowsException() {
        String key = "key";
        long total = -1;
        long occurrence = 2;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsValue(key, occurrence, total));
        assertTrue(exception.getMessage().contains("Stats total cannot be a negative value: " + total));
    }

    @Test
    void statisticsModelWithIdAndPositiveTotalGreaterThanOccurrence() {
        String key = "key";
        long total = 2;
        long occurrence = 1;

        StatisticsValue stats = new StatisticsValue(key, occurrence, total);

        assertThat(stats.getKey(), is(key));
        assertThat(stats.getHits(), is(occurrence));
        assertThat(stats.getPercentage(), is(calcPercentage(occurrence, total)));
    }

    private double calcPercentage(long occurrence, long total) {
        return ((double) occurrence / (double) total) * 100;
    }
}
