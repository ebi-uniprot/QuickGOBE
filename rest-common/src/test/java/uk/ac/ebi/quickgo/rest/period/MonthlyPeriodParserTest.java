package uk.ac.ebi.quickgo.rest.period;

import java.time.DateTimeException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests the creation of RemainingTimePeriod instances from MonthlyPeriodParser.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 15:27
 * Created with IntelliJ IDEA.
 */
public class MonthlyPeriodParserTest {

    private MonthlyPeriodParser monthlyPeriodParser = new MonthlyPeriodParser();

    @Test
    public void validInputStringWithDoubleAndSingleDigitDaysOfMonth(){
        String validInput="JANUARY(12)(21:30)-FEBRUARY(2)(18:15)";

        Period result = monthlyPeriodParser.parse(validInput);

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(RemainingTimePeriod.class));
    }

    @Test
    public void validInputStringWithDoubleAndSingleDigitTimes(){
        String validInput="JANUARY(12)(5:7)-FEBRUARY(2)(18:15)";

        Period result = monthlyPeriodParser.parse(validInput);

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(RemainingTimePeriod.class));
    }

    @Test
    public void nullInput(){
        Period result = monthlyPeriodParser.parse(null);

        assertThat(result, instanceOf(ZeroDurationPeriod.class));
    }

    @Test
    public void emptyInput(){
        Period result = monthlyPeriodParser.parse("");

        assertThat(result, instanceOf(ZeroDurationPeriod.class));
    }

    @Test
    public void toLittleData(){
        String invalidInput="JANUARY(12)(21:30)-";

        Period result = monthlyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test
    public void tooMuchData(){
        String invalidInput="JANUARY(12)(21:30)-FEBRUARY(2)(18:15)-DECEMBER(25)(3:00";

        Period result = monthlyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test
    public void wontMatchRegularExpression(){
        String invalidInput="BIMBLE(21:30)-FEBRUARY(21:30)";

        Period result = monthlyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test
    public void matchesRegularExpressionButNotAValidTime(){
        String invalidInput="JANUARY(4)(21:30)-FEBRUARY(5)(33:30)";

        Period result = monthlyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }

    @Test
    public void matchesRegularExpressionButNotAValidDayOfMonth(){
        String invalidInput="JANUARY(54)(21:30)-FEBRUARY(101)(10:30)";

        Period result = monthlyPeriodParser.parse(invalidInput);

        assertThat(result, nullValue());
    }
}
