package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;

/**
 * Hold range of two days of the Week {@link DayOfWeek}, provide information about this range.
 * @author Tony Wardell
 * Date: 17/05/2017
 * Time: 11:19
 * Created with IntelliJ IDEA.
 */
class DayOfWeekRange {

    private static final int ONE_DAY = 1;
    private final DayOfWeek start;
    private final DayOfWeek end;

    DayOfWeekRange(DayOfWeek start, DayOfWeek end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Does this range include the target day of the week
     * @param target DayOfWeek
     * @return boolean for inclusion.
     */
    boolean includes(DayOfWeek target) {
        DayOfWeek comparison = start;
        boolean checkedEnd = false;
        while (!checkedEnd) {
            if (comparison.equals(target)) {
                return true;
            }
            if (comparison.equals(end)) {
                checkedEnd = true;
            }
            comparison = comparison.plus(ONE_DAY);
        }
        return false;
    }
}
