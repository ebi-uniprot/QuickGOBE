package uk.ac.ebi.quickgo.rest.period;

import java.util.Optional;

/**
 * Interface for classes that interpret a String as a period definition.
 * @author Tony Wardell
 * Date: 25/04/2017
 * Time: 10:41
 * Created with IntelliJ IDEA.
 */
public interface PeriodParser {

    /**
     * Parse a String that contains a definition of a time period, to produce an instance of Period.
     * @param input String
     * @return instance an Optional of Period or an empty Optional if no valid period could be parsed.
     */
    Optional<Period> parse(String input);
}
