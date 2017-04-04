package uk.ac.ebi.quickgo.rest.cache;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Function;

/**
 * Holds algorithms to facilitate response caching.
 * <p>
 * Created by Tony on 04-Apr-17.
 */
public class CacheStrategy {

    public Function<LocalTime, Long> maxAgeCountDown(LocalTime s, LocalTime e) {
        return (n) -> {
            long maxAge;
            final LocalTime now = LocalTime.now();
            if (now.isAfter(s)) {
                maxAge = Duration.between(now, LocalTime.MAX).getSeconds() + Duration.between(LocalTime.MIN, e).getSeconds();
            } else {
                if (now.isBefore(e)) {
                    maxAge = Duration.between(now, e).getSeconds();
                } else {
                    maxAge = 0;
                }
            }
            assert maxAge >= 0;
            return maxAge;
        };
    }
}
