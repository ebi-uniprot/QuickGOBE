package uk.ac.ebi.quickgo.rest.period;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 * Abstract for classes that interpret a String as a period definition.
 * @author Tony Wardell
 * Date: 25/04/2017
 * Time: 10:41
 * Created with IntelliJ IDEA.
 */
public abstract class PeriodParser {
    private Logger LOGGER = LoggerFactory.getLogger(PeriodParser.class);
    private static final String TOO_SYMBOL = "-";
    private static final int REQUIRED_DATE_MODIFYING_INSTANCES = 2;

    /**
     * Parse a String that contains a definition of a time period, to produce an instance of Period.
     * @param input a string that contains a duration definition.
     * @return Optional of Period or an empty Optional if no valid period could be parsed.
     */
    public Optional<AlarmClock> parse(String input){
        if (Objects.nonNull(input) && !input.isEmpty()) {
            return getPeriod(input);
        }
        LOGGER.info("Period parse method sent null or empty value");
        return Optional.empty();
    }

    /**
     * Turn a String into and instance of DateModifying
     * @param input a string that contains a duration definition.
     * @return DateModifying instance.
     */
    protected abstract Optional<DateModifier> toDateModifier(String input);

    private Optional<AlarmClock> getPeriod(String input) {
        String[] fromTo = input.split(TOO_SYMBOL);
        if (fromTo.length == REQUIRED_DATE_MODIFYING_INSTANCES) {
            List<DateModifier> durationList = Arrays.stream(fromTo)
                                                    .map(this::toDateModifier)
                                                    .filter(Optional::isPresent)   //replace these two lines with
                                                    .map(Optional::get)            //.map(Optional::stream) in Java 9
                                                    .collect(toList());
            if (durationList.size() == REQUIRED_DATE_MODIFYING_INSTANCES) {
                return Optional.of(new AlarmClockImpl(durationList.get(0), durationList.get(1)));
            }
        }
        return Optional.empty();
    }
}
