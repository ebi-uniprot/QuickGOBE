package uk.ac.ebi.quickgo.rest.cache;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;

/**
 * This algorithm models the following situation - Start time of caching after end time. So the start time of caching
 * is occurs in one day, the end of the caching in the next. Here are the situations this class must deal with
 *
 * Before End Time: We are approaching the time of day when the data we use to populate requests is going to be
 * refreshed, therefore any subsequent requests to a RESTful service should have the following  header
 * <code>cache-control: max-age=0</code>
 * so User Agents or gateway caches don't cache this information (as it is likely to be out of data soon).
 *
 * After Start Time - we have high confidence data refresh will have taken place, and will not take place again until
 * after the end time the next day. Calculate the time left (in seconds) between a call to this method and Time A,
 * and set the cache-control header so this data can be cached by UAs, or gateway caches for this period. The response
 * HTTP header will be the following:
 * <code>cache-control: max-age=36000</code>
 * if there is exactly ten hours left between a call to this method and Time A.
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
 *
 * @author Tony Wardell
 * Date: 10/04/2017
 * Time: 15:53
 * Created with IntelliJ IDEA.
 */
@Deprecated
public class MaxAgeWhenStartTimeAfterEndTime implements Supplier<Duration> {

    private LocalTime start, end;

    public MaxAgeWhenStartTimeAfterEndTime(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    @Override public Duration get() {
        Duration remaining;
        final LocalTime now = LocalTime.now();
        if (now.isAfter(start)) {
            Duration durationLeftToday = Duration.between(now, LocalTime.MAX);
            Duration durationTomorrow = Duration.between(LocalTime.MIN, end);
            remaining = durationLeftToday.plus(durationTomorrow);
        } else {
            if (now.isBefore(end)) {
                remaining = Duration.between(now, end);
            } else {
                remaining = Duration.ZERO;
            }
        }
        assert !remaining.isNegative();
        return remaining;
    }
}
