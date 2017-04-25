package uk.ac.ebi.quickgo.rest.period;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Abstract for classes that interpret a String as a period definition.
 * @author Tony Wardell
 * Date: 25/04/2017
 * Time: 10:41
 * Created with IntelliJ IDEA.
 */
public abstract class PeriodParser {

    private static final String TOO_SYMBOL = "-";
    private static final int REQUIRED_DATE_MODIFYING_INSTANCES = 2;

    /**
     * Parse a String that contains a definition of a time period, to produce an instance of Period.
     * @param input a string that contains a duration definition.
     * @return Optional of Period or an empty Optional if no valid period could be parsed.
     */
    public abstract Optional<Period> parse(String input);

    /**
     * Turn a String into and instance of DateModifying
     * @param input a string that contains a duration definition.
     * @return DateModifying instance.
     */
    protected abstract Optional<DateModifying> toDateModifier(String input);

    /**
     * Create a period instance from a String that defines a from and to duration.
     * @param input a string that contains a duration definition.
     * @return
     */
    protected Optional<Period> getPeriod(String input) {
        String[] fromTo = input.split(TOO_SYMBOL);
        if (fromTo.length == REQUIRED_DATE_MODIFYING_INSTANCES) {
            List<DateModifying> durationList = Arrays.stream(fromTo)
                                                     .map(this::toDateModifier)
                                                     .filter(Optional::isPresent)   //replace these two lines with
                                                     .map(Optional::get)            //.map(Optional::stream) in Java 9
                                                     .collect(toList());
            if (durationList.size() == REQUIRED_DATE_MODIFYING_INSTANCES) {
                return Optional.of(new RemainingTimePeriod(durationList.get(0), durationList.get(1)));
            }
        }
        return Optional.empty();
    }
}
