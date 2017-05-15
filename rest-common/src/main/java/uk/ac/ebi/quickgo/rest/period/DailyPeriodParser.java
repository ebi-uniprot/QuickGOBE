package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Turn a string containing a definition for Day And Time into DayTime instance. Strings are required to be in the
 * format DAY(HH:MM)-DAY(HH:MM) which defines a start day and time to an end day and time. An example input string
 * could be <code>MONDAY(09:00)-FRIDAY(17:00)</code>
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:26
 * Created with IntelliJ IDEA.
 */
public class DailyPeriodParser extends PeriodParser{
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyPeriodParser.class);
    private static final String DAY_TIME_REGEX = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)\\(" +
            "([0-9]{2}):([0-9]{2})\\)";
    private static final Pattern DAY_TIME_PATTERN = Pattern.compile(DAY_TIME_REGEX);
    private static final int DAY_GROUP = 1;
    private static final int HOUR_GROUP = 2;
    private static final int MINUTE_GROUP = 3;
    private static final int EXPECTED_GROUP_COUNT = 3;

    @Override
    protected Optional<DateModifier> toDateModifier(String input) {
        try {
            Matcher periodMatcher = DAY_TIME_PATTERN.matcher(input);
            if(periodMatcher.matches() && periodMatcher.groupCount() == EXPECTED_GROUP_COUNT) {
                final int hours = Integer.parseInt(periodMatcher.group(HOUR_GROUP));
                final int minutes = Integer.parseInt(periodMatcher.group(MINUTE_GROUP));
                final DayTime dayTime = new DayTime(DayOfWeek.valueOf(periodMatcher.group(DAY_GROUP)),
                                                  LocalTime.of(hours, minutes));
                LOGGER.info("DailyPeriodParser toDateModifier from " + input + " to " + dayTime);
                return Optional.of(dayTime);
            }
        } catch (Exception e) {
            LOGGER.error("DailyPeriodParser parsed " + input + " but encountered an exception.", e);
        }
        return Optional.empty();
    }
}
