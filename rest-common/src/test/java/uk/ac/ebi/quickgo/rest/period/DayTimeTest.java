package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test instantiation of the DayTime class.
 *
 * @author Tony Wardell
 * Date: 18/04/2017
 * Time: 10:43
 * Created with IntelliJ IDEA.
 */
public class DayTimeTest {

    @Test
    public void instantiateDayTimeWithValidValues(){
       DayTime validDayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));

       assertThat(validDayTime, notNullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDayOfWeekCreatesException(){
        new DayTime(null, LocalTime.of(12, 37));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullLocalTimeCreatesException(){
        new DayTime(DayOfWeek.FRIDAY, null);
    }

    @Test
    public void modifySuccessfully(){
        DayTime dayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime compared = now.with(DayOfWeek.FRIDAY);
        LocalTime localTime = LocalTime.of(12, 37);
        compared = compared.with(localTime);

        LocalDateTime modified = dayTime.modify(now);

        assertThat(modified, equalTo(compared));
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyNullThrowsException(){
        DayTime dayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));

        dayTime.modify(null);
    }
}
