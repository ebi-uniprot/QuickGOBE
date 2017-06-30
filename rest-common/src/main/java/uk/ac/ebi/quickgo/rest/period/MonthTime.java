package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class MonthTime {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonthTime.class);
    private final MonthDay monthDay;
    private final LocalTime time;

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
    LocalDateTime modify(LocalDateTime target) {
        Preconditions.checkArgument(Objects.nonNull(target), "A target LocalDateTime cannot be null");
        LocalDateTime comparedDate = target.with(this.monthDay);
        return comparedDate.with(this.time);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MonthTime monthTime = (MonthTime) o;

        if (monthDay != null ? !monthDay.equals(monthTime.monthDay) : monthTime.monthDay != null) {
            return false;
        }
        return time != null ? time.equals(monthTime.time) : monthTime.time == null;
    }

    @Override public int hashCode() {
        int result = monthDay != null ? monthDay.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "MonthTime{" +
                "monthDay=" + monthDay +
                ", time=" + time +
                '}';
    }
}
