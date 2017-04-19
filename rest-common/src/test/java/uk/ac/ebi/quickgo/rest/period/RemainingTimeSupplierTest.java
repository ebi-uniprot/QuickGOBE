package uk.ac.ebi.quickgo.rest.period;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
@RunWith(MockitoJUnitRunner.class)
public class RemainingTimeSupplierTest {

    @Mock private
    Period allowedPeriod;
    private static final Duration ONE_HOUR = Duration.ofHours(1);

    @Test
    public void durationReturnedFromSinglePeriod() {
        final Duration ONE_HOUR = Duration.ofHours(1);
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(ONE_HOUR);

        RemainingTimeSupplier remainingTimeSupplier =
                new RemainingTimeSupplier(Collections.singletonList(allowedPeriod));

        assertThat(remainingTimeSupplier.get(), is(ONE_HOUR));
    }

    @Test
    public void durationReturnedFromThirdPeriod() {
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO);
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO);
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(ONE_HOUR);

        RemainingTimeSupplier remainingTimeSupplier = new RemainingTimeSupplier(Arrays.asList(allowedPeriod,
                                                                                              allowedPeriod,
                                                                                              allowedPeriod));

        assertThat(remainingTimeSupplier.get(), is(ONE_HOUR));
    }

    @Test
    public void noActivePeriodSoDurationIsZero() {
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO);
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO);
        when(allowedPeriod.remainingTime(any(LocalDateTime.class))).thenReturn(Duration.ZERO);

        RemainingTimeSupplier remainingTimeSupplier = new RemainingTimeSupplier(Arrays.asList(allowedPeriod,
                                                                                              allowedPeriod,
                                                                                              allowedPeriod));

        assertThat(remainingTimeSupplier.get(), is(Duration.ZERO));
    }

    @Test
    public void noDurationFoundFromEmptyPeriodList() {
        RemainingTimeSupplier remainingTimeSupplier = new RemainingTimeSupplier(new ArrayList<>());

        final Duration timeLeft = remainingTimeSupplier.get();

        assertThat(timeLeft, is(Duration.ZERO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionOfPeriodsWhenNullThrowsException() {
        new RemainingTimeSupplier(null);
    }
}