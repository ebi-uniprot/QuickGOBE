package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test instantiation and modification methods of the DayTime class.
 *
 * @author Tony Wardell
 * Date: 18/04/2017
 * Time: 10:43
 * Created with IntelliJ IDEA.
 */
class DayTimeTest {

    private DayTime dayTime;
    private LocalDateTime input;

    @BeforeEach
    void setup(){
        dayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));
        input = LocalDateTime.of(2017, 5, 17, 18, 47); //is a WED
    }


    @Test
    void instantiateDayTimeWithValidValues(){
       DayTime validDayTime = new DayTime(DayOfWeek.FRIDAY, LocalTime.of(12, 37));

       assertThat(validDayTime, notNullValue());
    }

    @Test
    void nullDayOfWeekCreatesException(){
        assertThrows(IllegalArgumentException.class, () -> new DayTime(null, LocalTime.of(12, 37)));
    }

    @Test
    void nullLocalTimeCreatesException(){
        assertThrows(IllegalArgumentException.class, () -> new DayTime(DayOfWeek.FRIDAY, null));
    }

    @Test
    void modifySuccessfullyToNext(){
        LocalDateTime expected = LocalDateTime.of(2017, 5, 19, 12, 37);

        LocalDateTime modified = dayTime.modifyToNext(input);

        assertThat(modified, equalTo(expected));
    }

    @Test
    void modifySuccessfullyToPrevious(){
        LocalDateTime expected = LocalDateTime.of(2017, 5, 12, 12, 37);

        LocalDateTime modified = dayTime.modifyToPrevious(input);

        assertThat(modified, equalTo(expected));
    }

    @Test
    void modifyToNextNullThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> dayTime.modifyToNext(null));
    }

    @Test
    void modifyToPreviousThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> dayTime.modifyToPrevious(null));
    }
}
