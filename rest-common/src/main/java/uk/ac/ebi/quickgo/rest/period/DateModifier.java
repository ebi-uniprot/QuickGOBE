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
public interface DateModifier {

    /**
     * Create a new instance of LocalDateTime based on a combination of the argument and the values/logic held by
     * implementing classes. The argument represents a specific instant in time, but a subclass could modify just the
     * time to a set value (for whatever purpose).
     * @param target
     * @return
     */
    LocalDateTime modify(LocalDateTime target);
}
