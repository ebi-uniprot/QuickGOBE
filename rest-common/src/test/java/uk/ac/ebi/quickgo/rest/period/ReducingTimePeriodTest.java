package uk.ac.ebi.quickgo.rest.period;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;

/**
 * Test CachingAllowedPeriod.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReducingTimePeriodTest {

    @Test
    public void secondsLeftWhenNowBetweenStartTimeAndEndTime(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(1);
        LocalDateTime end = now.plusMinutes(1);
        DayTime startDayTime = new DayTime(start.getDayOfWeek(), start.toLocalTime());
        DayTime endDayTime = new DayTime(end.getDayOfWeek(), end.toLocalTime());

        ReducingTimePeriod reducingTimePeriod = new ReducingTimePeriod(startDayTime, endDayTime);

        //Provide a little wriggle room for test
        long timeLeft = reducingTimePeriod.remainingTime(now).getSeconds();
        Long rangeLowPoint = timeLeft - 3;
        Long rangeHighPoint = timeLeft + 3;
        assertThat(timeLeft, is(both(greaterThan(rangeLowPoint)).and(lessThan(rangeHighPoint))));
    }

    @Test
    public void secondsLeftWhenComparingStartTimeAndEndTimeWithFixedTime(){

        LocalDateTime now = LocalDateTime.of(2012, 5, 9, 11, 59);
        LocalDateTime start = LocalDateTime.of(2012, 5, 9, 11, 58);
        LocalDateTime end = LocalDateTime.of(2012, 5, 9, 12, 00);
        DayTime startDayTime = new DayTime(start.getDayOfWeek(), start.toLocalTime());
        DayTime endDayTime = new DayTime(end.getDayOfWeek(), end.toLocalTime());

        ReducingTimePeriod reducingTimePeriod = new ReducingTimePeriod(startDayTime, endDayTime);

        //Provide a little wriggle room for test
        long timeLeft = reducingTimePeriod.remainingTime(now).getSeconds();
        assertThat(timeLeft, is(both(greaterThan(57L)).and(lessThan(63L))));
    }


    @Test
    public void durationZeroWhenNowAfterEndTime(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(2);
        LocalDateTime end = now.minusMinutes(1);
        DayTime startDayTime = new DayTime(start.getDayOfWeek(), start.toLocalTime());
        DayTime endDayTime = new DayTime(end.getDayOfWeek(), end.toLocalTime());

        ReducingTimePeriod reducingTimePeriod = new ReducingTimePeriod(startDayTime, endDayTime);

        assertThat(reducingTimePeriod.remainingTime(now), is(Duration.ZERO));
    }

    @Test
    public void durationZeroWhenNowBeforeStartTime(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusMinutes(2);
        LocalDateTime end = now.plusMinutes(1);
        DayTime startDayTime = new DayTime(start.getDayOfWeek(), start.toLocalTime());
        DayTime endDayTime = new DayTime(end.getDayOfWeek(), end.toLocalTime());

        ReducingTimePeriod reducingTimePeriod = new ReducingTimePeriod(startDayTime, endDayTime);

        assertThat(reducingTimePeriod.remainingTime(now), is(Duration.ZERO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfStartIsNull(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusMinutes(1);
        DayTime endDayTime = new DayTime(end.getDayOfWeek(), end.toLocalTime());

        new ReducingTimePeriod(null, endDayTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfEndIsNull(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusMinutes(2);
        DayTime startDayTime = new DayTime(start.getDayOfWeek(), start.toLocalTime());

        new ReducingTimePeriod(startDayTime, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfStartAndEndIsNull(){
        new ReducingTimePeriod(null, null);
    }
}
