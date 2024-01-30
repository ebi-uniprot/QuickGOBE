package uk.ac.ebi.quickgo.rest.period;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 19/04/2017
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
class MonthTimeTest {

    private static final int DAY_OF_MONTH = 18;
    private static final MonthDay MONTH_DAY = MonthDay.of(Month.APRIL, DAY_OF_MONTH);
    private static final int HOUR = 15;
    private static final int MINUTE = 10;
    private static final LocalTime LOCAL_TIME = LocalTime.of(HOUR, MINUTE);

    @Test
    void successfulCreation(){
        MonthTime monthTime = new MonthTime(MONTH_DAY, LOCAL_TIME);
        assertThat(monthTime, notNullValue());
    }

    @Test
    void unsuccessfulCreationWithNullMonthDay(){
        assertThrows(IllegalArgumentException.class, () -> new MonthTime(null, LOCAL_TIME));
    }

    @Test
    void unsuccessfulCreationWithNullLocalTime(){
        assertThrows(IllegalArgumentException.class, () -> new MonthTime(MONTH_DAY, null));
    }

    @Test
    void successfulModificationOfATargetLocalDateTime(){
        LocalDateTime input = LocalDateTime.of(2017, 5, 17, 12, 00);

        MonthTime monthTime = new MonthTime(MONTH_DAY, LOCAL_TIME);
        LocalDateTime modifiedDateTime = monthTime.modify(input);

        LocalDateTime expectedDateTime = LocalDateTime.of(2017, Month.APRIL, DAY_OF_MONTH, HOUR, MINUTE);
        assertThat(modifiedDateTime, is(equalTo(expectedDateTime)));
    }


    @Test
    void nullPassedToModifyThrowsException(){
        MonthTime monthTime = new MonthTime(MONTH_DAY, LOCAL_TIME);
        assertThrows(IllegalArgumentException.class, () -> monthTime.modify(null));
    }
}
