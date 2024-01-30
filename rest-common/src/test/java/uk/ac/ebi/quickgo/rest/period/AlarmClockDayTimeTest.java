package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test AlarmClockDayTime.
 *
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:07
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class AlarmClockDayTimeTest {

    private LocalDateTime input;
    private DayTime startDayTime;
    private DayTime endDayTime;
    private AlarmClockDayTime alarmClock;

    @BeforeEach
    void setup(){
       input = LocalDateTime.of(2017, 5, 17, 12, 00);   //WED
       startDayTime = new DayTime(DayOfWeek.MONDAY, LocalTime.of(12, 00));
       endDayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(11, 59));
       alarmClock = new AlarmClockDayTime(startDayTime, endDayTime);
    }

    @Test
    void secondsLeftWhenComparingStartTimeAndEndTimeWithFixedTime(){
        long timeLeft = alarmClock.remainingTime(input).getSeconds();

        assertThat(timeLeft, is(172740L));
    }

    @Test
    void durationZeroWhenInputAfterEndTime(){
        input = input.withDayOfMonth(19);

        final Duration remainingTime = alarmClock.remainingTime(input);

        assertThat(remainingTime, is(Duration.ZERO));
    }

    @Test
    void durationZeroWhenInputBeforeStartTime(){
        input = input.withDayOfMonth(15);
        input = input.withHour(11);
        input = input.withMinute(59);

        final Duration remainingTime = alarmClock.remainingTime(input);

        assertThat(remainingTime, is(Duration.ZERO));
    }

    @Test
    void exceptionIfStartIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new AlarmClockDayTime(null, endDayTime));
    }

    @Test
    void exceptionIfEndIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new AlarmClockDayTime(startDayTime, null));
    }

    @Test
    void exceptionIfStartAndEndIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new AlarmClockDayTime(null, null));
    }
}
