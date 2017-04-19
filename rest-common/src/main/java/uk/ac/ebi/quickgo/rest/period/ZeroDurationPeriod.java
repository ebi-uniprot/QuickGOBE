package uk.ac.ebi.quickgo.rest.period;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 15:20
 * Created with IntelliJ IDEA.
 */
public class ZeroDurationPeriod implements Period{

    @Override public Duration remainingTime(LocalDateTime target) {
        return Duration.ZERO;
    }
}
