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
 * Test calculation of duration when start time is after the end time - i.e the duration wraps across midnight.
 *
 * @author Tony Wardell
 * Date: 05/04/2017
 * Time: 13:26
 * Created with IntelliJ IDEA.
 */
public class MaxAgeWhenStartTimeAfterEndTimeTest {

    @Test
    public void secondsLeftWhenNowBetweenStartOfDayAndEndTime(){

        LocalTime now = LocalTime.now();
        LocalTime endTime = now.plusMinutes(1);
        LocalTime startTime = now.plusMinutes(2);

        if(startTime.isBefore(endTime)){
            //we've running this test at midnight, the time has wrapped and we need to bail out
            return;
        }
        Duration totalTimeLeft =  Duration.between(now, endTime);
        //Provide a little wiggle room for test
        Duration rangeLowPoint = totalTimeLeft.minusSeconds(3);
        Duration rangeHighPoint = totalTimeLeft.plusSeconds(3);

        Supplier<Duration> maxAge = new MaxAgeWhenStartTimeAfterEndTime(startTime, endTime);
        Duration timeLeft = maxAge.get();

        assertThat(timeLeft.getSeconds(), is(both(greaterThan(rangeLowPoint.getSeconds())).and(lessThan
                                                                                                       (rangeHighPoint.getSeconds()))));
    }

    @Test
    public void secondsLeftForMaxAgeWhenNowAfterStartTimeAndBeforeEndOfDay(){

        LocalTime now = LocalTime.now();
        LocalTime endTime = now.minusMinutes(2);
        LocalTime startTime = now.minusMinutes(1);
        if(startTime.isBefore(endTime)){
            //we've running this test at midnight, the time has wrapped and we need to bail out
            return;
        }
        Duration midnightTillEndTime = Duration.between(LocalTime.MIN, endTime);
        Duration timeLeftToday = Duration.between(now, LocalTime.MAX);
        Duration totalTimeLeft = timeLeftToday.plus(midnightTillEndTime);
        Duration rangeLowPoint = totalTimeLeft.minusSeconds(3);
        Duration rangeHighPoint = totalTimeLeft.plusSeconds(3);

        Supplier<Duration> maxAge = new MaxAgeWhenStartTimeAfterEndTime(startTime, endTime);

        assertThat(maxAge.get().getSeconds(), is(both(greaterThan(rangeLowPoint.getSeconds())).and(lessThan(rangeHighPoint.getSeconds
                ()))));
    }

    @Test
    public void secondsSetToZeroWhenNowBetweenEndAndStartTimes(){

        LocalTime now = LocalTime.now();
        LocalTime endTime = now.minusHours(1);
        LocalTime startTime = now.plusHours(1);

        Supplier<Duration> maxAge = new MaxAgeWhenStartTimeAfterEndTime(startTime, endTime);
        assertThat(maxAge.get(), is(Duration.ZERO));
    }
}
