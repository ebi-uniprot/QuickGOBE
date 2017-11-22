package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

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

        new StatisticsByType(type,0);
    }

    @Test
    public void emptyTypeThrowsException() {
        String type = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null or empty");

        new StatisticsByType(type,0);
    }

    @Test
    public void nullStatisticsValueThrowsException() {
        String type = "type";
        StatisticsValue value = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats value cannot be null");

        StatisticsByType statsType = new StatisticsByType(type,0);
        statsType.addValue(value);
    }

    @Test
    public void addedStatisticsValueIsRetrievedCorrectly() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type,12);

        assertThat(statsType.getDistinctValueCount(), is(12));
    }

    @Test
    public void addedStatisticsValueRoundedIfValueGreaterThan10K() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10001);

        assertThat(statsType.getDistinctValueCount(), is(10000));
    }

    @Test
    public void addedStatisticsValueRoundedIfValueGreaterThan10KTest2() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 11999);

        assertThat(statsType.getDistinctValueCount(), is(11000));
    }

    @Test
    public void addedStatisticsValueRoundedIfValueGreaterThan100K() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 487934);

        assertThat(statsType.getDistinctValueCount(), is(487000));
    }

    @Test
    public void addedStatisticsValueRoundedIfValueGreaterThan1m() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 7487934);

        assertThat(statsType.getDistinctValueCount(), is(7487000));
    }
}
