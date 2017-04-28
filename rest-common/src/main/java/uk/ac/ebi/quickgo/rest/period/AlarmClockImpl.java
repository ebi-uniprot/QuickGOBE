package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An implementation of a {@link AlarmClock} which uses two values of {@link DateModifier} as start and end points to
 * determine remaining time.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 12:46
 * Created with IntelliJ IDEA.
 */
public class AlarmClockImpl implements AlarmClock {

    private final DateModifier start;
    private final DateModifier end;

    AlarmClockImpl(DateModifier start, DateModifier end) {
        Preconditions.checkArgument(Objects.nonNull(start), "The RemainingTimePeriod constructor start parameter " +
                "must not be null.");
        Preconditions.checkArgument(Objects.nonNull(end),"The RemainingTimePeriod constructor end parameter " +
                "must not be null.");
        this.start = start;
        this.end = end;
    }

    @Override
    public Duration remainingTime(LocalDateTime target) {
        LocalDateTime startDateTime = start.modify(target);
        LocalDateTime endDateTime = end.modify(target);
        Duration remaining;
        if (target.isAfter(startDateTime) && target.isBefore(endDateTime)){
            remaining = Duration.between(target, endDateTime);
        }else{
            remaining = Duration.ZERO;
        }
        return remaining;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlarmClockImpl that = (AlarmClockImpl) o;

        if (start != null ? !start.equals(that.start) : that.start != null) {
            return false;
        }
        return end != null ? end.equals(that.end) : that.end == null;
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
