package uk.ac.ebi.quickgo.rest.cache;

import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.Test;

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
       new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDayOfWeekCreatesException(){
        new DayTime(null, LocalTime.of(12, 37));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullLocalTimeCreatesException(){
        new DayTime(DayOfWeek.FRIDAY, null);
    }
}
