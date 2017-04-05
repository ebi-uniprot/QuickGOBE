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
public class CacheStrategyTest {

    @Test
    public void secondsLeftWhenNowBetweenStartOfDayAndEndTime(){

        LocalTime now = LocalTime.now();
        LocalTime endTime = now.plusMinutes(1);
        LocalTime startTime = now.plusMinutes(2);

        if(startTime.isBefore(endTime)){
            //we've running this test at midnight, the time has wrapped and we need to bail out
            return;
        }
        Long timeLeftToday = Duration.between(now, endTime).getSeconds();
        Long totalTimeLeft = timeLeftToday;
        //Provide a little wiggle room for test
        Long rangeLowPoint = totalTimeLeft - 3;
        Long rangeHighPoint = totalTimeLeft + 3;

        Supplier<String> timeProvider = CacheStrategy.maxAgeTimeLeft(startTime, endTime);
        Long timeLeft = Long.parseLong(timeProvider.get());

        assertThat(timeLeft, is(both(greaterThan(rangeLowPoint)).and(lessThan(rangeHighPoint))));
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
        Long midnightTillEndTime = Duration.between(LocalTime.MIN, endTime).getSeconds();
        Long timeLeftToday = Duration.between(now, LocalTime.MAX).getSeconds();
        Long totalTimeLeft = timeLeftToday + midnightTillEndTime;
        Long rangeLowPoint = totalTimeLeft - 3;
        Long rangeHighPoint = totalTimeLeft + 3;

        Supplier<String> timeProvider = CacheStrategy.maxAgeTimeLeft(startTime, endTime);
        Long timeLeft = Long.parseLong(timeProvider.get());

        assertThat(timeLeft, is(both(greaterThan(rangeLowPoint)).and(lessThan(rangeHighPoint))));

    }

    @Test
    public void secondsSetToZeroWhenNowBetweenEndAndStartTimes(){

        LocalTime now = LocalTime.now();
        LocalTime endTime = now.minusHours(1);
        LocalTime startTime = now.plusHours(1);

        Supplier<String> timeProvider = CacheStrategy.maxAgeTimeLeft(startTime, endTime);

        assertThat(timeProvider.get(), is("0"));
    }
}
