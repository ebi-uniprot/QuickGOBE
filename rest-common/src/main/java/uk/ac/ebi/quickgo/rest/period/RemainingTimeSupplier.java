package uk.ac.ebi.quickgo.rest.period;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

/**
 * From a collection of {@link Period}s, find the first one to cover this instant in time, and if one is found,
 * find out how much time is left before this period ends.
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 12:45
 * Created with IntelliJ IDEA.
 */
public class RemainingTimeSupplier {

    private Collection<Period> periodCollection;

    public RemainingTimeSupplier(Collection<Period> periodCollection) {
        Preconditions.checkArgument(nonNull(periodCollection), "The collection of periods to check must not " +
                "be null");
        this.periodCollection = periodCollection;
    }

    /**
     * Calculate and return the {@link Duration} between {@link LocalDateTime} 'now' and the {@link Period}s held by
     * this instance. Return first non-zero Duration, or a Duration of Zero.
     * @return Duration left of any active periods, if they exist.
     */
    public Duration getDuration() {
        LocalDateTime now = LocalDateTime.now();
        Optional<Duration> remainingTime = periodCollection.stream()
                                                           .map(p -> p.remainingTime(now))
                                                           .filter(d -> !d.isZero())
                                                           .findFirst();
        return remainingTime.orElse(Duration.ZERO);
    }
}
