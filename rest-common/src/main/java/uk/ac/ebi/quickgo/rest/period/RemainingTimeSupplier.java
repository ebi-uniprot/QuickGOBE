package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * From a collection of {@link AlarmClock}s, find the first one to cover this instant in time, and if one is found,
 * find out how much time is left before this period ends.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 12:45
 * Created with IntelliJ IDEA.
 */
public class RemainingTimeSupplier {

    private final Collection<AlarmClock> alarmClocks;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemainingTimeSupplier.class);

    public RemainingTimeSupplier(Collection<AlarmClock> alarmClocks) {
        Preconditions.checkArgument(nonNull(alarmClocks), "The collection of periods to check must not " +
                "be null");
        this.alarmClocks = alarmClocks;
    }

    /**
     * Calculate and return the {@link Duration} between {@link LocalDateTime} 'now' and the {@link AlarmClock}s held by
     * this instance. Return first non-zero Duration, or a Duration of zero.
     * @return Duration left of any active periods, if they exist.
     */
    public Duration getDuration() {
        LocalDateTime now = LocalDateTime.now();
        Optional<Duration> remainingTime = alarmClocks.stream()
                                                      .map(p -> p.remainingTime(now))
                                                      .filter(duration -> !duration.isZero())
                                                      .findFirst();
        LOGGER.debug("Get Duration calculation has result in a remaining time of " + remainingTime);
        return remainingTime.orElse(Duration.ZERO);
    }
}
