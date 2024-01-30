package uk.ac.ebi.quickgo.annotation.model;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link StatisticsByType} class.
 */
class StatisticsByTypeTest {

    @Test
    void nullTypeThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsByType(null, 0));
        assertTrue(exception.getMessage().contains("Statistics type cannot be null or empty"));
    }

    @Test
    void emptyTypeThrowsException() {
        String type = "";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsByType(type, 0));
        assertTrue(exception.getMessage().contains("Statistics type cannot be null or empty"));
    }

    @Test
    void negativeApproximateCountThrowsException() {
        String type = "type";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsByType(type, -10));
        assertTrue(exception.getMessage().contains("Distinct Value Count should be be greater than zero."));
    }

    @Test
    void nullStatisticsValueThrowsException() {
        String type = "type";

        StatisticsByType statsType = new StatisticsByType(type, 0);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> statsType.addValue(null));
        assertTrue(exception.getMessage().contains("Stats value cannot be null"));
    }

    @Test
    void zeroApproximateCountIsOK() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 0);

        assertThat(statsType.getApproximateCount(), is(0));
    }

    @Test
    void approximateCountBelow10001IsUnchanged() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10000);

        assertThat(statsType.getApproximateCount(), is(10000));
    }

    @Test
    void approximateCountRoundedDownIfValueGreaterThan10KAndEndsWith101To499() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10001);

        assertThat(statsType.getApproximateCount(), is(10000));
    }

    @Test
    void approximateCountRoundedDownIfEndsWith499OrLess() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10499);

        assertThat(statsType.getApproximateCount(), is(10000));
    }

    @Test
    void approximateCountRoundedUpIfEndsWith500OrGreater() {
        String type = "type";
        StatisticsByType statsType = new StatisticsByType(type, 10500);

        assertThat(statsType.getApproximateCount(), is(11000));
    }

    @Test
    void returnRealStatsSizeIfApproximateCountIsFewer() {
        StatisticsByType statsType = new StatisticsByType("goId", 20);

        IntStream.rangeClosed(1, 25)
                .forEach(v -> statsType.addValue(new StatisticsValue(Integer.toString(v), v, v)));

        assertThat(statsType.getApproximateCount(), is(25));
    }

    @Test
    void returnApproximateCountIfRealStatsSizeIsFewer() {
        StatisticsByType statsType = new StatisticsByType("goId", 25);

        IntStream.rangeClosed(1, 20)
                .forEach(v -> statsType.addValue(new StatisticsValue(Integer.toString(v), v, v)));

        assertThat(statsType.getApproximateCount(), is(25));
    }
}
