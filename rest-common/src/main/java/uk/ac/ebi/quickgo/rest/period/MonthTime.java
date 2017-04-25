package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * A union of {@link MonthDay} and {@link LocalTime} to represent a particular day of month, month and time
 * combination without reference to year. This class doesn't represent an instant of time, but a point in time once
 * per year.
 *
 * @author Tony Wardell
 * Date: 19/04/2017
 * Time: 11:19
 * Created with IntelliJ IDEA.
 */
public class MonthTime implements DateModifier {

    private MonthDay monthDay;
    private LocalTime time;

    MonthTime(MonthDay monthDay, LocalTime time) {
        Preconditions.checkArgument(Objects.nonNull(monthDay), "Invalid monthDay parameter passed to " +
                "MonthTime constructor. Parameter is null, it should be a valid MonthDay instance.");
        Preconditions.checkArgument(Objects.nonNull(time), "Invalid time parameter passed to MonthTime constructor. " +
                "Parameter was null, it should be a valid LocalTime instance.");
        this.monthDay = monthDay;
        this.time = time;
    }

    /**
     * Modify the targetTime to be an instant defined by the values held in this instance.
     * @param target to modify.
     * @return a particular instant in time.
     */
    public LocalDateTime modify(LocalDateTime target) {
        Preconditions.checkArgument(Objects.nonNull(target), "A target LocalDateTime cannot be null");
        LocalDateTime comparedDate = target.with(this.monthDay);
        return comparedDate.with(this.time);
    }
}
