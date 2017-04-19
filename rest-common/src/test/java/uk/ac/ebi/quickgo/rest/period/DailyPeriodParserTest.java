package uk.ac.ebi.quickgo.rest.period;

import java.time.DateTimeException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
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

    private DailyPeriodParser dailyPeriodParser = new DailyPeriodParser();

    @Before
    public void setup(){
        dailyPeriodParser = new DailyPeriodParser();
    }

    @Test
    public void validInputString(){
        String validInput="MONDAY(21:30)-TUESDAY(21:30)";

        Period result = dailyPeriodParser.parse(validInput);
        assertThat(result, notNullValue());
        assertThat(result, instanceOf(ReducingDailyPeriod.class));
    }

    @Test
    public void nullInput(){
        Period result = dailyPeriodParser.parse(null);

        assertThat(result, instanceOf(ZeroDurationPeriod.class));
    }

    @Test
    public void emptyInput(){
        Period result = dailyPeriodParser.parse("");

        assertThat(result, instanceOf(ZeroDurationPeriod.class));
    }

    @Test
    public void toLittleData(){
        String invalidInput="MONDAY(21:30)-";

        Period result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test
    public void tooMuchData(){
        String invalidInput="MONDAY(21:30)-TUESDAY(21:30)-WEDNESDAY(21:30)";

        Period result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test
    public void wontMatchRegularExpression(){
        String invalidInput="MONDAY(21:30)-FEBRUARY(21:30)";

        Period result = dailyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test(expected = DateTimeException.class)
    public void matchesRegularExpressionButNotAValidTime(){
        String invalidInput="MONDAY(21:30)-TUESDAY(33:30)";

        dailyPeriodParser.parse(invalidInput);
    }
}
