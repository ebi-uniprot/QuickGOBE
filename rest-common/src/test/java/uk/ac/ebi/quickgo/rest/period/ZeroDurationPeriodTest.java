package uk.ac.ebi.quickgo.rest.period;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tony Wardell
 * Date: 18/04/2017
 * Time: 11:43
 * Created with IntelliJ IDEA.
 */
public class ZeroDurationPeriodTest {

    @Test
    public void remainingTimeHasZeroDuration(){
        ZeroDurationPeriod zeroDurationPeriod = new ZeroDurationPeriod();
        assertThat(zeroDurationPeriod.remainingTime(LocalDateTime.of(2005, 2, 12, 14, 37)), is(Duration.ZERO));
    }

    @Test
    public void remainingTimeHasZeroDurationEvenIfWeCompareToANullLocalDateTimed(){
        ZeroDurationPeriod zeroDurationPeriod = new ZeroDurationPeriod();
        assertThat(zeroDurationPeriod.remainingTime(null), is(Duration.ZERO));
    }
}
