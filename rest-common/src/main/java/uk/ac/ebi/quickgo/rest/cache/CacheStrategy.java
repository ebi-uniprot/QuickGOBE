package uk.ac.ebi.quickgo.rest.cache;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;

/**
 * Holds algorithms to facilitate response caching.
 * <p>
 * Created by Tony on 04-Apr-17.
 */
public class CacheStrategy {

    /**
     * This algorithm models the following situation
     *
     * Time A - we are approaching the time of day when the data we use to populate requests is going to be
     * refreshed, therefore any subsequent requests to a RESTful service should have the following  header
     * <code>cache-control: max-age=0</code>
     * so User Agents, gateway caches don't cache this information (as it is likely to be out of data soon).
     *
     * Time B - we have high confidence data refresh will have taken place, and will not place until after Time A the
     * next day. Calculate the time left (in seconds) between a call to this method and Time A, and set the cache
     * control header so this data can be cached by UAs, or gateway caches for this period. The response HTTP header
     * will be the following
     * <code>cache-control: max-age=36000</code>
     * if there is exact ten hours left between a call to this method and Time A.
     *
     * See <a href="https://jakearchibald.com/2016/caching-best-practices/">Caching best practices & max-age
     * gotchas</a>,
     * <a href="https://www.mnot.net/cache_docs/">Caching Tutorial for Web Authors and Webmasters</a>,
     * <a href="https://devcenter.heroku.com/articles/increasing-application-performance-with-http-cache-headers">Increasing Application Performance with HTTP Cache Headers | Heroku Dev Center</a>
     *
     * @param start The time of day after which a max-age time will be calculated, up until the end time, the next day
     * @param end The time of day after which the max-age will be reported as zero, so no more caching takes place
     * for all subsequent requests.
     * @return a String that holds the number of seconds left before the cut-of time after which requests will not be
     * cached.
     */
    public static Supplier<String> maxAgeTimeLeft(LocalTime start, LocalTime end) {

        Preconditions.checkArgument(start.isAfter(end));

        return () -> {
            long maxAge;
            final LocalTime now = LocalTime.now();
            if (now.isAfter(start)) {
                maxAge = Duration.between(now, LocalTime.MAX).getSeconds() + Duration.between(LocalTime.MIN, end).getSeconds();
            } else {
                if (now.isBefore(end)) {
                    maxAge = Duration.between(now, end).getSeconds();
                } else {
                    maxAge = 0;
                }
            }
            assert maxAge >= 0;
            return Long.toString(maxAge);
        };
    }
}
