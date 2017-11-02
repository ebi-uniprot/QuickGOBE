package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JANUARY(12)(5:7)-FEBRUARY(2)(18:15)
 *
 * @author Tony Wardell
 * Date: 16/05/2017
 * Time: 13:06
 * Created with IntelliJ IDEA.
 */
public class AlarmClockMonthTime implements AlarmClock {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmClockDayTime.class);
    private final MonthTime start;
    private final MonthTime end;

    AlarmClockMonthTime(MonthTime start, MonthTime end) {
        Preconditions.checkArgument(Objects.nonNull(start), "The RemainingTimePeriod constructor start parameter " +
                "must not be null.");
        Preconditions.checkArgument(Objects.nonNull(end), "The RemainingTimePeriod constructor end parameter " +
                "must not be null.");
        this.start = start;
        this.end = end;
    }

    @Override
    public Duration remainingTime(LocalDateTime target) {
        LOGGER.debug("Calculating remaining time.");
        return comparedModifiedTimes(target);
    }

    private Duration comparedModifiedTimes(LocalDateTime target) {
        LocalDateTime startDateTime = start.modify(target);
        LocalDateTime endDateTime = end.modify(target);
        if (endDateTime.isBefore(startDateTime)) {
            endDateTime = endDateTime.plusYears(1);
        }
        LOGGER.debug("Calculating remaining time between " + target + " and " + endDateTime);

        Duration remaining;
        if (startDateTime.isBefore(target) && endDateTime.isAfter(target)) {
            remaining = Duration.between(target, endDateTime);
        } else {
            remaining = Duration.ZERO;
        }
        LOGGER.debug("Remaining time is " + remaining);
        return remaining;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlarmClockMonthTime that = (AlarmClockMonthTime) o;

        return (start != null ? start.equals(that.start) : that.start == null) &&
                (end != null ? end.equals(that.end) : that.end == null);
    }

    @Override public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "RemainingTimePeriod{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
