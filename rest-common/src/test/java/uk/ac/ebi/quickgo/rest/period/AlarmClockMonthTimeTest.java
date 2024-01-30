package uk.ac.ebi.quickgo.rest.period;

import java.time.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test calculation of remaining time calculated by AlarmClockMonthTime.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class AlarmClockMonthTimeTest {

    private MonthTime monthTimeMarch;
    private MonthTime monthTimeOctober;
    private AlarmClockMonthTime alarmClock;
    private LocalDateTime input;

    @BeforeEach
    void setup(){
        //MARCH(3)(10:15)-OCTOBER(2)(19:45)
        monthTimeMarch = new MonthTime(MonthDay.of(3, 1), LocalTime.of(10, 15));
        monthTimeOctober =  new MonthTime(MonthDay.of(10, 11), LocalTime.of(19, 45));
        alarmClock = new AlarmClockMonthTime(monthTimeMarch, monthTimeOctober);
        input = LocalDateTime.of(2017, 5, 17, 12, 00);
    }

    @Test
    void secondsLeftWhenStartAndEndInSameYear(){
        long timeLeft = alarmClock.remainingTime(input).getSeconds();

        assertThat(timeLeft, is(12728700L));
    }

    //OCTOBER(2)(10:15)-MARCH(3)(19:45)
    @Test
    void secondsLeftWhenEndInYearAfterStart(){
        AlarmClockMonthTime alarmClockOctToMarch = new AlarmClockMonthTime(monthTimeOctober, monthTimeMarch);
        LocalDateTime inputNov = LocalDateTime.of(2017, 11, 17, 12, 00);

        long timeLeft = alarmClockOctToMarch.remainingTime(inputNov).getSeconds();

        assertThat(timeLeft, is(8979300L));
    }

    @Test
    void noTimeLeftWhenJustBeforeStart(){
        LocalDateTime inputBeforeStart = LocalDateTime.of(2017, 3, 1, 10, 14);

        long timeLeft = alarmClock.remainingTime(inputBeforeStart).getSeconds();

        assertThat(timeLeft, is(0L));
    }

    @Test
    void noTimeLeftWhenJustAfterEnd(){
        LocalDateTime inputAfterEnd = LocalDateTime.of(2017, 10, 11, 19, 46);

        long timeLeft = alarmClock.remainingTime(inputAfterEnd).getSeconds();

        assertThat(timeLeft, is(0L));
    }

    @Test
    void exceptionIfEndIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new AlarmClockMonthTime(monthTimeMarch, null));
    }

    @Test
    void exceptionIfStartIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new AlarmClockMonthTime(null, monthTimeMarch));
    }

    @Test
    void exceptionIfStartAndEndIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new AlarmClockMonthTime(null, null));
    }
}
