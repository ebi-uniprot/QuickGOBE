package uk.ac.ebi.quickgo.rest.period;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test DayOfWeekRange.
 * @author Tony Wardell
 * Date: 16/05/2017
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
class DayOfWeekRangeTest {

    @Test
    void testIncludes(){
        assertTrue(new DayOfWeekRange(DayOfWeek.SATURDAY, DayOfWeek.SATURDAY).includes(DayOfWeek.SATURDAY));
        assertTrue(new DayOfWeekRange(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).includes(DayOfWeek.SATURDAY));
        assertTrue(new DayOfWeekRange(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).includes(DayOfWeek.SUNDAY));
        assertTrue(new DayOfWeekRange(DayOfWeek.MONDAY, DayOfWeek.SUNDAY).includes(DayOfWeek.WEDNESDAY));
        assertTrue(new DayOfWeekRange(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY).includes(DayOfWeek.FRIDAY));
        assertTrue(new DayOfWeekRange(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY).includes(DayOfWeek.WEDNESDAY));
        assertTrue(new DayOfWeekRange(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY).includes(DayOfWeek.MONDAY));

        assertFalse(new DayOfWeekRange(DayOfWeek.SATURDAY, DayOfWeek.SATURDAY).includes(DayOfWeek.SUNDAY));
        assertFalse(new DayOfWeekRange(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).includes(DayOfWeek.WEDNESDAY));
        assertFalse(new DayOfWeekRange(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY).includes(DayOfWeek.THURSDAY));
    }
}
