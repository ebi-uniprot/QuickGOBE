package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
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

        new StatisticsByType(type, 0);
    }

    @Test
    public void emptyTypeThrowsException() {
        String type = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null or empty");

        new StatisticsByType(type, 0);
    }

    @Test
    public void negativeDistinctValueCountThrowsException() {
        String type = "type";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Distinct Value Count should be be greater than zero.");

        new StatisticsByType(type, -10);
    }

    @Test
    public void nullStatisticsValueThrowsException() {
        String type = "type";
        StatisticsValue value = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats value cannot be null");

        StatisticsByType statsType = new StatisticsByType(type, 0);
        statsType.addValue(value);
    }

    @Test
    public void zeroDistinctValueCountIsOK() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 0);

        assertThat(statsType.getDistinctValueCount(), is(0));
    }

    @Test
    public void distinctValueCountBelow10001IsUnchanged() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10000);

        assertThat(statsType.getDistinctValueCount(), is(10000));
    }

    @Test
    public void distinctValueCountRoundedDownIfValueGreaterThan10KAndEndsWith101To499() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10001);

        assertThat(statsType.getDistinctValueCount(), is(10000));
    }

    @Test
    public void distinctValueCountRoundedDownIfEndsWith499OrLess() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10499);

        assertThat(statsType.getDistinctValueCount(), is(10000));
    }

    @Test
    public void distinctValueCountRoundedUpIfEndsWith500OrGreater() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10500);

        assertThat(statsType.getDistinctValueCount(), is(11000));
    }
}
