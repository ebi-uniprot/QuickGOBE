package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * Data structure holds union of day of week and time. Used to declare events that have a daily occurrence at a set
 * time.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 16:15
 * Created with IntelliJ IDEA.
 */
class DayTime {

    final DayOfWeek dayOfWeek;
    private final LocalTime time;

    /**
     * Given a target time, this method returns a {@link LocalDateTime} instance that has the same day and time as
     * target.
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

    private LocalDateTime modify(LocalDateTime target, TemporalAdjuster toDay) {
        Preconditions.checkArgument(Objects.nonNull(target), "A target LocalDateTime cannot be null");
        LocalDateTime comparedDate = target.with(toDay);
        comparedDate = comparedDate.with(this.time);
        return comparedDate;
    }

    LocalDateTime modifyToPrevious(LocalDateTime target) {
        TemporalAdjuster toDay = TemporalAdjusters.previousOrSame(this.dayOfWeek);
        return modify(target, toDay);
    }

    LocalDateTime modifyToNext(LocalDateTime target) {
        TemporalAdjuster toDay = TemporalAdjusters.nextOrSame(this.dayOfWeek);
        return modify(target, toDay);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DayTime dayTime = (DayTime) o;

        if (dayOfWeek != dayTime.dayOfWeek) {
            return false;
        }
        return time != null ? time.equals(dayTime.time) : dayTime.time == null;
    }

    @Override public int hashCode() {
        int result = dayOfWeek != null ? dayOfWeek.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "DayTime{" +
                "dayOfWeek=" + dayOfWeek +
                ", time=" + time +
                '}';
    }
}
