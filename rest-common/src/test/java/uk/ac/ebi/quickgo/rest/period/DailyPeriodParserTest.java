package uk.ac.ebi.quickgo.rest.period;

import java.util.Optional;
import org.junit.Test;

import static java.util.Optional.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Tests the creation of DayTime instances from PeriodParser.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
public class DailyPeriodParserTest {

    private final DailyPeriodParser dailyPeriodParser = new DailyPeriodParser();

    @Test
    public void validInputString(){
        String validInput="MONDAY(21:30)-TUESDAY(21:30)";

        Optional<Period> result = dailyPeriodParser.parse(validInput);
        assertThat(result.get(), instanceOf(RemainingTimePeriod.class));
    }

    @Test
    public void nullInput(){
        Optional<Period> result = dailyPeriodParser.parse(null);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void emptyInput(){
        Optional<Period> result = dailyPeriodParser.parse("");

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void toLittleData(){
        String invalidInput="MONDAY(21:30)-";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void tooMuchData(){
        String invalidInput="MONDAY(21:30)-TUESDAY(21:30)-WEDNESDAY(21:30)";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void wontMatchRegularExpression(){
        String invalidInput="MONDAY(21:30)-FEBRUARY(21:30)";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }

    @Test
    public void matchesRegularExpressionButNotAValidTime(){
        String invalidInput="MONDAY(21:30)-TUESDAY(33:30)";

        Optional<Period> result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, equalTo(empty()));
    }
}
