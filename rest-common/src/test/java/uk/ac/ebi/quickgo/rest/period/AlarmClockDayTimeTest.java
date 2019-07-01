package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test AlarmClockDayTime.
 *
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AlarmClockDayTimeTest {

    private LocalDateTime input;
    private DayTime startDayTime;
    private DayTime endDayTime;
    private AlarmClockDayTime alarmClock;

    @Before
    public void setup(){
       input = LocalDateTime.of(2017, 5, 17, 12, 00);   //WED
       startDayTime = new DayTime(DayOfWeek.MONDAY, LocalTime.of(12, 00));
       endDayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(11, 59));
       alarmClock = new AlarmClockDayTime(startDayTime, endDayTime);
    }

    @Test
    public void secondsLeftWhenComparingStartTimeAndEndTimeWithFixedTime(){
        long timeLeft = alarmClock.remainingTime(input).getSeconds();

        assertThat(timeLeft, is(172740L));
    }

    @Test
    public void durationZeroWhenInputAfterEndTime(){
        input = input.withDayOfMonth(19);

        final Duration remainingTime = alarmClock.remainingTime(input);

        assertThat(remainingTime, is(Duration.ZERO));
    }

    @Test
    public void durationZeroWhenInputBeforeStartTime(){
        input = input.withDayOfMonth(15);
        input = input.withHour(11);
        input = input.withMinute(59);

        final Duration remainingTime = alarmClock.remainingTime(input);

        assertThat(remainingTime, is(Duration.ZERO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfStartIsNull(){
        new AlarmClockDayTime(null, endDayTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfEndIsNull(){
        new AlarmClockDayTime(startDayTime, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfStartAndEndIsNull(){
        new AlarmClockDayTime(null, null);
    }
}
