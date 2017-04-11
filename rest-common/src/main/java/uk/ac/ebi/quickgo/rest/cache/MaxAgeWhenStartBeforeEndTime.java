package uk.ac.ebi.quickgo.rest.cache;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;

/**
 * This algorithm models the following situation - Start time of caching before end time. So the start and end of
 * caching occurs in one day. Here are the situations this class must deal with
 *
 * After Start Time Before End Time: Re-indexing is not going to take place until after End time. Give clients the time
 * in seconds until End Time will be reached.
 *
 * After End Time and before Start Time: We are around the indexing time. We tell all respondents not to cache - max
 * age is set to zero.
 *
 * See <a href="https://jakearchibald.com/2016/caching-best-practices/">Caching best practices & max-age
 * gotchas</a>,
 * <a href="https://www.mnot.net/cache_docs/">Caching Tutorial for Web Authors and Webmasters</a>,
 * <a href="https://devcenter.heroku.com/articles/increasing-application-performance-with-http-cache-headers">Increasing Application Performance with HTTP Cache Headers | Heroku Dev Center</a>
 *
 * @return a String that holds the number of seconds left before the cut-off time after which requests will not be
 * cached.
 * @author Tony Wardell
 * Date: 10/04/2017
 * Time: 15:47
 * Created with IntelliJ IDEA.
 */
public class MaxAgeWhenStartBeforeEndTime implements Supplier<Duration>{

    private LocalTime start, end;

    public MaxAgeWhenStartBeforeEndTime(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    @Override public Duration get() {
        Duration remaining;
        final LocalTime now = LocalTime.now();
        if (now.isAfter(start) && now.isBefore(end)) {
            remaining = Duration.between(now, end);
        } else {
            remaining = Duration.ZERO;
        }
        assert !remaining.isNegative();
        return remaining;
    }
}
