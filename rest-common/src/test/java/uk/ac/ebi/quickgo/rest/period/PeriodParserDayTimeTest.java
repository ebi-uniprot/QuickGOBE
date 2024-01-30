package uk.ac.ebi.quickgo.rest.period;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests the creation of DayTime instances from DailyPeriodParser.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
class PeriodParserDayTimeTest {

    private final PeriodParserDayTime periodParserDayTime = new PeriodParserDayTime();

    @Test
    void validInputCreatesPeriodSuccessfully(){
        String validInput="MONDAY(21:30)-TUESDAY(21:30)";
        DayTime start = new DayTime(DayOfWeek.MONDAY, LocalTime.of(21, 30));
        DayTime end = new DayTime(DayOfWeek.TUESDAY, LocalTime.of(21, 30));
        AlarmClockDayTime remainingTimePeriod = new AlarmClockDayTime(start, end);

        Optional<AlarmClock> result = periodParserDayTime.parse(validInput);

        assertThat(result.isPresent(), equalTo(true));
        assertThat(result.get(), equalTo(remainingTimePeriod));
    }

    @Test
    void nullInputCreatesEmptyPeriod(){
        Optional<AlarmClock> result = periodParserDayTime.parse(null);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void emptyInputCreatesEmptyPeriod(){
        Optional<AlarmClock> result = periodParserDayTime.parse("");

        assertThat(result, equalTo(empty()));
    }

    @Test
    void missingEndValueCreatesEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-";

        Optional<AlarmClock> result = periodParserDayTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void tooMuchDataCreatesEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-TUESDAY(21:30)-WEDNESDAY(21:30)";

        Optional<AlarmClock> result = periodParserDayTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void invalidDayOfWeekCreateEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-FEBRUARY(21:30)";

        Optional<AlarmClock> result = periodParserDayTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void invalidEndTimeCreatesEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-TUESDAY(33:30)";

        Optional<AlarmClock> result = periodParserDayTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }
}
