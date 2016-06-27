package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Tests the behaviour of the {@link StatisticsByType} class.
 */
public class StatisticsByTypeTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullTypeThrowsException() {
        String type = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null or empty");

        new StatisticsByType(type);
    }

    @Test
    public void emptyTypeThrowsException() {
        String type = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null or empty");

        new StatisticsByType(type);
    }

    @Test
    public void nullStatisticsValueThrowsException() {
        String type = "type";
        StatisticsValue value = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats value cannot be null");

        StatisticsByType statsType = new StatisticsByType(type);
        statsType.addValue(value);
    }

    @Test
    public void addedStatisticsValueIsRetrievedCorrectly() {
        String type = "type";
        StatisticsValue value = new StatisticsValue("key", 0, 0);

        StatisticsByType statsType = new StatisticsByType(type);
        statsType.addValue(value);

        assertThat(statsType.getValues(), contains(value));
    }
}
