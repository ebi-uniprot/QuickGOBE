package uk.ac.ebi.quickgo.rest.period;

import java.time.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test calculation of remaining time calculated by AlarmClockMonthTime.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AlarmClockMonthTimeTest {

    private MonthTime monthTimeMarch;
    private MonthTime monthTimeOctober;
    private AlarmClockMonthTime alarmClock;
    private LocalDateTime input;

    @Before
    public void setup(){
        //MARCH(3)(10:15)-OCTOBER(2)(19:45)
        monthTimeMarch = new MonthTime(MonthDay.of(3, 1), LocalTime.of(10, 15));
        monthTimeOctober =  new MonthTime(MonthDay.of(10, 11), LocalTime.of(19, 45));
        alarmClock = new AlarmClockMonthTime(monthTimeMarch, monthTimeOctober);
        input = LocalDateTime.of(2017, 5, 17, 12, 00);
    }

    @Test
    public void secondsLeftWhenStartAndEndInSameYear(){
        long timeLeft = alarmClock.remainingTime(input).getSeconds();

        assertThat(timeLeft, is(12728700L));
    }

    //OCTOBER(2)(10:15)-MARCH(3)(19:45)
    @Test
    public void secondsLeftWhenEndInYearAfterStart(){
        AlarmClockMonthTime alarmClockOctToMarch = new AlarmClockMonthTime(monthTimeOctober, monthTimeMarch);
        LocalDateTime inputNov = LocalDateTime.of(2017, 11, 17, 12, 00);

        long timeLeft = alarmClockOctToMarch.remainingTime(inputNov).getSeconds();

        assertThat(timeLeft, is(8979300L));
    }

    @Test
    public void noTimeLeftWhenJustBeforeStart(){
        LocalDateTime inputBeforeStart = LocalDateTime.of(2017, 3, 1, 10, 14);

        long timeLeft = alarmClock.remainingTime(inputBeforeStart).getSeconds();

        assertThat(timeLeft, is(0L));
    }

    @Test
    public void noTimeLeftWhenJustAfterEnd(){
        LocalDateTime inputAfterEnd = LocalDateTime.of(2017, 10, 11, 19, 46);

        long timeLeft = alarmClock.remainingTime(inputAfterEnd).getSeconds();

        assertThat(timeLeft, is(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfEndIsNull(){
        new AlarmClockMonthTime(monthTimeMarch, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfStartIsNull(){
        new AlarmClockMonthTime(null, monthTimeMarch);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIfStartAndEndIsNull(){
        new AlarmClockMonthTime(null, null);
    }
}
