package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test instantiation and modification methods of the DayTime class.
 *
 * @author Tony Wardell
 * Date: 18/04/2017
 * Time: 10:43
 * Created with IntelliJ IDEA.
 */
public class DayTimeTest {

    private DayTime dayTime;
    private LocalDateTime input;

    @Before
    public void setup(){
        dayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));
        input = LocalDateTime.of(2017, 5, 17, 18, 47); //is a WED
    }


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
    public void modifySuccessfullyToNext(){
        LocalDateTime expected = LocalDateTime.of(2017, 5, 19, 12, 37);

        LocalDateTime modified = dayTime.modifyToNext(input);

        assertThat(modified, equalTo(expected));
    }

    @Test
    public void modifySuccessfullyToPrevious(){
        LocalDateTime expected = LocalDateTime.of(2017, 5, 12, 12, 37);

        LocalDateTime modified = dayTime.modifyToPrevious(input);

        assertThat(modified, equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyToNextNullThrowsException(){
        dayTime.modifyToNext(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyToPreviousThrowsException(){
        dayTime.modifyToPrevious(null);
    }
}
