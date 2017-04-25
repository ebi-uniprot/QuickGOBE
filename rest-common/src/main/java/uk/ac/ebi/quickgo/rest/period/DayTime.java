package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * Data structure holds union of day of week and time. Used to declare events that have a daily occurrence at a set
 * time.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 16:15
 * Created with IntelliJ IDEA.
 */
class DayTime implements DateModifier {

    private final DayOfWeek dayOfWeek;
    private final LocalTime time;

    /**
     * Create an instance of DayTime from {@link DayOfWeek} and {@link LocalTime} instance.
     * @param dayOfWeek this class represents.
     * @param time this class represents.
     */
    DayTime(DayOfWeek dayOfWeek, LocalTime time) {
        Preconditions.checkArgument(Objects.nonNull(dayOfWeek), "Invalid DayOfWeek parameter passed to " +
                "DayTime constructor. Parameter is null, it should be a valid DayOfWeek instance.");
        Preconditions.checkArgument(Objects.nonNull(time), "Invalid time parameter passed to DayTime constructor. " +
                "Parameter was null, it should be a valid LocalTime instance.");
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    /**
     * Modify the targetTime to be an instant defined by the values held in this instance.
     * @param target to modify.
     * @return a particular instant in time.
     */
    public LocalDateTime modify(LocalDateTime target) {
        Preconditions.checkArgument(Objects.nonNull(target), "A target LocalDateTime cannot be null");
        LocalDateTime comparedDate = target.with(this.dayOfWeek);
        return comparedDate.with(this.time);
    }
}
