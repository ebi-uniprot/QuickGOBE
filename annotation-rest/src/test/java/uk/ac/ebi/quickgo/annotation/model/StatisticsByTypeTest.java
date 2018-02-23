package uk.ac.ebi.quickgo.annotation.model;

import java.util.stream.IntStream;
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
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null or empty");

        new StatisticsByType(null, 0);
    }

    @Test
    public void emptyTypeThrowsException() {
        String type = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null or empty");

        new StatisticsByType(type, 0);
    }

    @Test
    public void negativeApproximateCountThrowsException() {
        String type = "type";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Distinct Value Count should be be greater than zero.");

        new StatisticsByType(type, -10);
    }

    @Test
    public void nullStatisticsValueThrowsException() {
        String type = "type";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats value cannot be null");

        StatisticsByType statsType = new StatisticsByType(type, 0);
        statsType.addValue(null);
    }

    @Test
    public void zeroApproximateCountIsOK() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 0);

        assertThat(statsType.getApproximateCount(), is(0));
    }

    @Test
    public void approximateCountBelow10001IsUnchanged() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10000);

        assertThat(statsType.getApproximateCount(), is(10000));
    }

    @Test
    public void approximateCountRoundedDownIfValueGreaterThan10KAndEndsWith101To499() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10001);

        assertThat(statsType.getApproximateCount(), is(10000));
    }

    @Test
    public void approximateCountRoundedDownIfEndsWith499OrLess() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10499);

        assertThat(statsType.getApproximateCount(), is(10000));
    }

    @Test
    public void approximateCountRoundedUpIfEndsWith500OrGreater() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10500);

        assertThat(statsType.getApproximateCount(), is(11000));
    }

    @Test
    public void returnRealStatsSizeIfApproximateCountIsFewer() {
        StatisticsByType statsType = new StatisticsByType("goId", 20);

        IntStream.rangeClosed(1, 25)
                .forEach(v -> statsType.addValue(new StatisticsValue(Integer.toString(v), v, v)));

        assertThat(statsType.getApproximateCount(), is(25));
    }

    @Test
    public void returnApproximateCountIfRealStatsSizeIsFewer() {
        StatisticsByType statsType = new StatisticsByType("goId", 25);

        IntStream.rangeClosed(1, 20)
                .forEach(v -> statsType.addValue(new StatisticsValue(Integer.toString(v), v, v)));

        assertThat(statsType.getApproximateCount(), is(25));
    }
}
