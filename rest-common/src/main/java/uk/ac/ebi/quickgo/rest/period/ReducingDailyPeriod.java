package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * An implementation of a {@link Period} which uses two values for {@link DayTime} as start and end points to
 * determine remaining time.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 12:46
 * Created with IntelliJ IDEA.
 */
public class ReducingDailyPeriod implements Period {

    @NotNull
    private final DateModifying start;
    @NotNull
    private final DateModifying end;

    ReducingDailyPeriod(DateModifying start, DateModifying end) {
        Preconditions.checkArgument(Objects.nonNull(start), "The ReducingDailyPeriod constructor start parameter " +
                "must not be null.");
        Preconditions.checkArgument(Objects.nonNull(end),"The ReducingDailyPeriod constructor end parameter " +
                "must not be null.");
        this.start = start;
        this.end = end;
    }

    public Duration remainingTime(LocalDateTime target) {
        LocalDateTime startDateTime = start.toInstant(target);
        LocalDateTime endDateTime = end.toInstant(target);
        Duration remaining;
        if (target.isAfter(startDateTime) && target.isBefore(endDateTime)){
            remaining = Duration.between(target, endDateTime);
        }else{
            remaining = Duration.ZERO;
        }
        return remaining;
    }
}
