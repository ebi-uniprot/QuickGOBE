package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;

/**
 * Easily testable class for determining if a day of week value falls within a day of week range.
 * @author Tony Wardell
 * Date: 16/05/2017
 * Time: 10:41
 * Created with IntelliJ IDEA.
 */
class PeriodComparison{

    private static final long ONE_TEMPORAL_UNIT = 1;

    static boolean dayBetweenStartAndEnd(DayOfWeek start, DayOfWeek end, DayOfWeek target){
        DayOfWeek comparison = start;
        boolean checkedEnd = false;
        while(!checkedEnd) {
            if (comparison.equals(target)){
                return true;
            }
            if(comparison.equals(end)){
                checkedEnd = true;
            }
            comparison = comparison.plus(ONE_TEMPORAL_UNIT);
        }
        return false;
    }
}
