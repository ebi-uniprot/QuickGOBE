package uk.ac.ebi.quickgo.rest.cache;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;

/**
 * Test methods in the CacheStrategy class.
 *
 * @author Tony Wardell
 * Date: 05/04/2017
 * Time: 13:26
 * Created with IntelliJ IDEA.
 */
public class MaxAgeWhenStartBeforeEndTimeTest {

    @Test
    public void secondsLeftWhenNowBetweenStartTimeAndEndTime(){

        LocalTime now = LocalTime.now();
        LocalTime startTime = now.minusMinutes(1);
        LocalTime endTime = now.plusMinutes(1);

        if(startTime.isAfter(endTime)){
            //Evidently this test is running at midnight - the time has wrapped and we need to bail out
            return;
        }
        Long totalTimeLeft = Duration.between(now, endTime).getSeconds();

        Supplier<Duration> maxAge = new MaxAgeWhenStartBeforeEndTime(startTime, endTime);
        long timeLeft = maxAge.get().getSeconds();

        //Provide a little wriggle room for test
        Long rangeLowPoint = totalTimeLeft - 3;
        Long rangeHighPoint = totalTimeLeft + 3;
        assertThat(timeLeft, is(both(greaterThan(rangeLowPoint)).and(lessThan(rangeHighPoint))));
    }

    @Test
    public void durationZeroWhenNowfterEndTime(){

        LocalTime now = LocalTime.now();
        LocalTime startTime = now.minusMinutes(2);
        LocalTime endTime = now.minusMinutes(1);
        if(startTime.isAfter(endTime)){
            //we've running this test at midnight, the time has wrapped and we need to bail out
            return;
        }
        Supplier<Duration> maxAge = new MaxAgeWhenStartBeforeEndTime(startTime, endTime);
        assertThat(maxAge.get(), is(Duration.ZERO));
    }

    @Test
    public void durationZeroWhenNowBeforeStartTime(){

        LocalTime now = LocalTime.now();
        LocalTime startTime = now.plusMinutes(1);
        LocalTime endTime = now.plusMinutes(2);
        if(startTime.isAfter(endTime)){
            //we've running this test at midnight, the time has wrapped and we need to bail out
            return;
        }
        Supplier<Duration> maxAge = new MaxAgeWhenStartBeforeEndTime(startTime, endTime);
        assertThat(maxAge.get(), is(Duration.ZERO));
    }
}
