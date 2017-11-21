package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link StatisticsValue} class.
 */
public class StatisticsValueTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullIdThrowsException() throws Exception {
        String key = null;
        long total = 2;
        long occurrence = 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats key cannot be null or empty");

        new StatisticsValue(key, occurrence, total);
    }

    @Test
    public void emptyIdThrowsException() throws Exception {
        String key = "";
        long total = 2;
        long occurrence = 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats key cannot be null or empty");

        new StatisticsValue(key, occurrence, total);
    }

    @Test
    public void negativeOccurrenceThrowsException() throws Exception {
        String key = "key";
        long total = 2;
        long occurrence = -1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats hits cannot be a negative value: " + occurrence);

        new StatisticsValue(key, occurrence, total);
    }

    @Test
    public void negativeTotalThrowsException() throws Exception {
        String key = "key";
        long total = -1;
        long occurrence = 2;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats total cannot be a negative value: " + total);

        new StatisticsValue(key, occurrence, total);
    }

    @Test
    public void statisticsModelWithIdAndPositiveTotalGreaterThanOccurrence() throws Exception {
        String key = "key";
        long total = 2;
        long occurrence = 1;

        StatisticsValue stats = new StatisticsValue(key, occurrence, total);

        assertThat(stats.getKey(), is(key));
        assertThat(stats.getHits(), is(occurrence));
        assertThat(stats.getPercentage(), is(calcPercentage(occurrence, total)));
    }

    private double calcPercentage(long occurrence, long total) {
        return (double) occurrence / (double) total;
    }
}
