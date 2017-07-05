package uk.ac.ebi.quickgo.rest.period;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Allows a calculation of remaining time.
 *
 * @author Tony Wardell
 * Date: 12/04/2017
 * Time: 14:12
 * Created with IntelliJ IDEA.
 */
public interface AlarmClock {

    /**
     * What is the Duration left from the date and time held by the implementing class, and the target LocalDateTime
     * @param target Date and time for which to calculate remaining time.
     * @return Remaining time as a Duration.
     */
    Duration remainingTime(LocalDateTime target);
}
