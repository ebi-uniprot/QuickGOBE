package uk.ac.ebi.quickgo.rest.period;

import java.time.LocalDateTime;

/**
 * Implementing classes must be able to use themselves to modify a target date to reflect their state.
 *
 * @author Tony Wardell
 * Date: 19/04/2017
 * Time: 12:00
 * Created with IntelliJ IDEA.
 */
public interface DateModifying {

    LocalDateTime toInstant(LocalDateTime target);
}
