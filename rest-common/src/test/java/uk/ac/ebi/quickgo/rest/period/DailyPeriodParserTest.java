package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.Test;

import static java.util.Optional.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests the creation of DayTime instances from DailyPeriodParser.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
public class DailyPeriodParserTest {

    private final DailyPeriodParser dailyPeriodParser = new DailyPeriodParser();

    @Test
    public void validInputCreatesPeriodSuccessfully(){
        String validInput="MONDAY(21:30)-TUESDAY(21:30)";
        DateModifier start = new DayTime(DayOfWeek.MONDAY, LocalTime.of(21, 30));
        DateModifier end = new DayTime(DayOfWeek.TUESDAY, LocalTime.of(21, 30));
        RemainingTimePeriod remainingTimePeriod = new RemainingTimePeriod(start, end);

        Optional<Period> result = dailyPeriodParser.parse(validInput);

        assertThat(result.get(), equalTo(remainingTimePeriod));
    }

    @Test
    public void nullInputCreatesEmptyPeriod(){
        Optional<Period> result = dailyPeriodParser.parse(null);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void emptyInputCreatesEmptyPeriod(){
        Optional<Period> result = dailyPeriodParser.parse("");

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void missingEndValueCreatesEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void tooMuchDataCreatesEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-TUESDAY(21:30)-WEDNESDAY(21:30)";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void invalidDayOfWeekCreateEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-FEBRUARY(21:30)";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void invalidEndTimeCreatesEmptyPeriod(){
        String invalidInput="MONDAY(21:30)-TUESDAY(33:30)";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }
}
