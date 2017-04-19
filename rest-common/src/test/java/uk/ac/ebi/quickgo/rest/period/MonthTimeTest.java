package uk.ac.ebi.quickgo.rest.period;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tony Wardell
 * Date: 19/04/2017
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
public class MonthTimeTest {

    private static final int DAY_OF_MONTH = 18;
    private static final MonthDay MONTH_DAY = MonthDay.of(Month.APRIL, DAY_OF_MONTH);
    private static final int HOUR = 15;
    private static final int MINUTE = 10;
    private static final LocalTime TEA_TIME = LocalTime.of(HOUR, MINUTE);

    @Test
    public void successfulCreation(){
        new MonthTime(MONTH_DAY, TEA_TIME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulCreationWithNullMonthDay(){
        new MonthTime(null, TEA_TIME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulCreationWithNullLocalTiime(){
        new MonthTime(MONTH_DAY, null);
    }

    @Test
    public void successfulModificationOfATargetLocalDateTime(){
        MonthTime monthTime = new MonthTime(MONTH_DAY, TEA_TIME);
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime modifiedDateTime = monthTime.toInstant(now);

        assertThat(modifiedDateTime.getYear(), is(now.getYear()));
        assertThat(modifiedDateTime.getMonth(), is(Month.APRIL));
        assertThat(modifiedDateTime.getDayOfMonth(), is(DAY_OF_MONTH));
        assertThat(modifiedDateTime.getHour(), is(HOUR));
        assertThat(modifiedDateTime.getMinute(), is(MINUTE));
    }

}
