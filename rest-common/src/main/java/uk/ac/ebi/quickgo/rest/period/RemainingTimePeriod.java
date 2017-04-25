package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An implementation of a {@link Period} which uses two values of {@link DateModifier} as start and end points to
 * determine remaining time.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 12:46
 * Created with IntelliJ IDEA.
 */
public class RemainingTimePeriod implements Period {

    private final DateModifier start;
    private final DateModifier end;

    RemainingTimePeriod(DateModifier start, DateModifier end) {
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
}
