package uk.ac.ebi.quickgo.rest.period;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Test RemainingTimeSupplier.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 13:57
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class RemainingTimeSupplierTest {

    @Mock
    private AlarmClock alarmClock;
    private static final Duration ONE_HOUR = Duration.ofHours(1);
    private static final Duration TWO_HOUR = Duration.ofHours(2);

    @Test
    void durationReturnedFromSinglePeriod() {
        when(alarmClock.remainingTime(any(LocalDateTime.class))).thenReturn(ONE_HOUR);

        RemainingTimeSupplier remainingTimeSupplier =
                new RemainingTimeSupplier(Collections.singletonList(alarmClock));

        assertThat(remainingTimeSupplier.getDuration(), is(ONE_HOUR));
    }

    @Test
    void durationReturnedFromFirstNonZeroPeriod() {
        when(alarmClock.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO)
                                                                .thenReturn(Duration.ZERO)
                                                                .thenReturn(ONE_HOUR)
                                                                .thenReturn(TWO_HOUR);

        RemainingTimeSupplier remainingTimeSupplier = new RemainingTimeSupplier(Arrays.asList(alarmClock,
                                                                                              alarmClock,
                                                                                              alarmClock));

        assertThat(remainingTimeSupplier.getDuration(), is(ONE_HOUR));
    }

    @Test
    void noActivePeriodSoDurationIsZero() {
        when(alarmClock.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO)
                                                                .thenReturn(Duration.ZERO)
                                                                .thenReturn(Duration.ZERO);

        RemainingTimeSupplier remainingTimeSupplier = new RemainingTimeSupplier(Arrays.asList(alarmClock,
                                                                                              alarmClock,
                                                                                              alarmClock));

        assertThat(remainingTimeSupplier.getDuration(), is(Duration.ZERO));
    }

    @Test
    void noDurationFoundFromEmptyPeriodList() {
        RemainingTimeSupplier remainingTimeSupplier = new RemainingTimeSupplier(new ArrayList<>());

        final Duration timeLeft = remainingTimeSupplier.getDuration();

        assertThat(timeLeft, is(Duration.ZERO));
    }

    @Test
    void collectionOfPeriodsWhenNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new RemainingTimeSupplier(null));
    }
}
