package uk.ac.ebi.quickgo.rest.cache;

import java.time.DateTimeException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
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
public class PeriodParserTest {

    private PeriodParser periodParser = new PeriodParser();

    @Before
    public void setup(){
        periodParser = new PeriodParser();
    }

    @Test
    public void validInputString(){
        String validInput="MONDAY(21:30)-TUESDAY(21:30)";

        Period result = periodParser.parse(validInput);
        assertThat(result, notNullValue());
        assertThat(result, instanceOf(CachingAllowedPeriod.class));
    }

    @Test
    public void nullInput(){
        Period result = periodParser.parse(null);

        assertThat(result, instanceOf(ZeroDurationPeriod.class));
    }

    @Test
    public void emptyInput(){
        Period result = periodParser.parse("");

        assertThat(result, instanceOf(ZeroDurationPeriod.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toLittleData(){
        String invalidInput="MONDAY(21:30)-";

        periodParser.parse(invalidInput);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooMuchData(){
        String invalidInput="MONDAY(21:30)-TUESDAY(21:30)-WEDNESDAY(21:30)";

        periodParser.parse(invalidInput);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wontMatchRegularExpression(){
        String invalidInput="MONDAY(21:30)-FEBRUARY(21:30)";

        periodParser.parse(invalidInput);
    }

    @Test(expected = DateTimeException.class)
    public void matchesRegularExpressionButNotAValidTime(){
        String invalidInput="MONDAY(21:30)-TUESDAY(33:30)";

        periodParser.parse(invalidInput);
    }
}
