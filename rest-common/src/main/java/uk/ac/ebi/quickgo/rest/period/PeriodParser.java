package uk.ac.ebi.quickgo.rest.period;

import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract for classes that interpret a String as a period definition.
 * @author Tony Wardell
 * Date: 25/04/2017
 * Time: 10:41
 * Created with IntelliJ IDEA.
 */
public abstract class PeriodParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodParser.class);
    static final String TO_SYMBOL = "-";
    static final int REQUIRED_DATE_MODIFYING_INSTANCES = 2;

    /**
     * Parse a String that contains a definition of a time period, to produce an instance of Period.
     * @param input a string that contains a duration definition.
     * @return Optional of Period or an empty Optional if no valid period could be parsed.
     */
    public Optional<AlarmClock> parse(String input) {
        if (Objects.nonNull(input) && !input.isEmpty()) {
            return getPeriod(input);
        }
        LOGGER.error("PeriodParser parse method sent null or empty value");
        return Optional.empty();
    }

    protected abstract Optional<AlarmClock> getPeriod(String input);
}
