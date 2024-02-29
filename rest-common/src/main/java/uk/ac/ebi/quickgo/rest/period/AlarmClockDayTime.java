package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a {@link AlarmClock} which uses two values of {@link DayTime} as start and end points to
 * determine remaining time.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 12:46
 * Created with IntelliJ IDEA.
 */
public class AlarmClockDayTime implements AlarmClock {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmClockDayTime.class);
    private final DayTime start;
    private final DayTime end;

    AlarmClockDayTime(DayTime start, DayTime end) {
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
        Duration remaining = Duration.ZERO;
        final DayOfWeekRange dayOfWeekRange = new DayOfWeekRange(start.dayOfWeek, end.dayOfWeek);
        if (dayOfWeekRange.includes(target.getDayOfWeek())) {
            remaining = comparedModifiedTimes(target);
        }
        return remaining;
    }

    private Duration comparedModifiedTimes(LocalDateTime target) {
        LocalDateTime startDateTime = start.modifyToPrevious(target);
        LocalDateTime endDateTime = end.modifyToNext(target);
        LOGGER.debug("AlarmClockImpl calculating remaining time between " + target + " and " + endDateTime);

        Duration remaining;
        if (startDateTime.isBefore(target) && endDateTime.isAfter(target)) {
            remaining = Duration.between(target, endDateTime);
        } else {
            remaining = Duration.ZERO;
        }
        LOGGER.debug("Remaining time is %s".formatted(remaining));
        return remaining;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlarmClockDayTime that = (AlarmClockDayTime) o;

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
