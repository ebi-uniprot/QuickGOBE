package uk.ac.ebi.quickgo.rest.period;

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
     * @return instance of Period or null if no valid period could be parsed. Nulls are returned so parser can be
     * chained if necessary, any non-null values are therefore valid to be used.
     */
    Period parse(String input);
}
