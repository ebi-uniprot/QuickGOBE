package uk.ac.ebi.quickgo.rest.period;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests the creation of RemainingTimePeriod instances from MonthlyPeriodParser.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
class PeriodParserMonthTimeTest {

    private final PeriodParserMonthTime periodParserMonthTime = new PeriodParserMonthTime();

    @Test
    void validInputStringWithDoubleAndSingleDigitDaysOfMonth(){
        String validInput="JANUARY(12)(21:30)-FEBRUARY(2)(18:15)";
        MonthTime start = new MonthTime(MonthDay.of(Month.JANUARY, 12), LocalTime.of(21, 30));
        MonthTime end  = new MonthTime(MonthDay.of(Month.FEBRUARY, 2), LocalTime.of(18, 15));
        AlarmClockMonthTime alarmClockMonthTime = new AlarmClockMonthTime(start, end);

        Optional<AlarmClock> result = periodParserMonthTime.parse(validInput);

        assertThat(result.get(), equalTo(alarmClockMonthTime));
    }

    @Test
    void validInputStringWithDoubleAndSingleDigitTimesSuccessfullyCreatesPeriod(){
        String validInput="JANUARY(12)(5:7)-FEBRUARY(2)(18:15)";
        MonthTime start = new MonthTime(MonthDay.of(Month.JANUARY, 12), LocalTime.of(5, 7));
        MonthTime end  = new MonthTime(MonthDay.of(Month.FEBRUARY, 2), LocalTime.of(18, 15));
        AlarmClockMonthTime alarmClockMonthTime = new AlarmClockMonthTime(start, end);

        Optional<AlarmClock> result = periodParserMonthTime.parse(validInput);

        assertThat(result.get(), equalTo(alarmClockMonthTime));
    }

    @Test
    void nullInputCreatesEmptyPeriod(){
        Optional<AlarmClock> result = periodParserMonthTime.parse(null);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void emptyInputCreatesEmptyPeriod(){
        Optional<AlarmClock> result = periodParserMonthTime.parse("");

        assertThat(result, equalTo(empty()));
    }

    @Test
    void missingEndValueCreatesEmptyPeriod(){
        String invalidInput="JANUARY(12)(21:30)-";

        Optional<AlarmClock> result = periodParserMonthTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void tooMuchDataCreatesEmptyPeriod(){
        String invalidInput="JANUARY(12)(21:30)-FEBRUARY(2)(18:15)-DECEMBER(25)(3:00";

        Optional<AlarmClock> result = periodParserMonthTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void invalidMonthCreatesEmptyPeriod(){
        String invalidInput="BIMBLE(21:30)-FEBRUARY(21:30)";

        Optional<AlarmClock> result = periodParserMonthTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void invalidTimeCreatesEmptyPeriod(){
        String invalidInput="JANUARY(4)(21:30)-FEBRUARY(5)(33:30)";

        Optional<AlarmClock> result = periodParserMonthTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    void invalidDayOfMonthCreatesEmptyPeriod(){
        String invalidInput="JANUARY(54)(21:30)-FEBRUARY(101)(10:30)";

        Optional<AlarmClock> result = periodParserMonthTime.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }
}
