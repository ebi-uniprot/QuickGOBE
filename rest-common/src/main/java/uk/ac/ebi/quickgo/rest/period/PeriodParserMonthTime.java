package uk.ac.ebi.quickgo.rest.period;

import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 * Turn a string containing a definition for month, date And time into Period instance. Strings are required to be in
 * the format MONTH(DATE)(HH:MM)-MONTH(DATE)(HH:MM) which defines a start month, day of month and time to an end
 * month, day of month and time.
 * An example input string
 * could be <code>JANUARY(15)(09:00)-JUNE(19)(17:00)</code>
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:26
 * Created with IntelliJ IDEA.
 */
public class PeriodParserMonthTime extends PeriodParser {
    private final Logger LOGGER = LoggerFactory.getLogger(PeriodParserMonthTime.class);
    private static final String MONTH_DATE_TIME_REGEX = "^" +
            "(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER)\\(([0-9]{1,2})" +
            "\\)\\(([0-9]{1,2}):([0-9]{1,2})\\)";
    private static final Pattern MONTH_DATE_TIME_PATTERN = Pattern.compile(MONTH_DATE_TIME_REGEX);
    private static final int MONTH_GROUP = 1;
    private static final int DATE_GROUP = 2;
    private static final int HOUR_GROUP = 3;
    private static final int MINUTE_GROUP = 4;
    private static final int EXPECTED_GROUP_COUNT = 4;

    protected Optional<AlarmClock> getPeriod(String input) {
        String[] fromTo = input.split(TO_SYMBOL);
        if (fromTo.length == REQUIRED_DATE_MODIFYING_INSTANCES) {
            List<MonthTime> durationList = Arrays.stream(fromTo)
                                                 .map(this::mapToMonthTime)
                                                 .filter(Optional::isPresent)   //replace these two lines with
                                                 .map(Optional::get)            //.map(Optional::stream) in Java 9
                                                 .collect(toList());
            LOGGER.debug("Created durationList " + durationList);
            if (durationList.size() == REQUIRED_DATE_MODIFYING_INSTANCES) {
                return Optional.of(new AlarmClockMonthTime(durationList.get(0), durationList.get(1)));
            }
        }
        return Optional.empty();
    }

    private Optional<MonthTime> mapToMonthTime(String input) {
        try {
            Matcher periodMatcher = MONTH_DATE_TIME_PATTERN.matcher(input);
            if (periodMatcher.matches() && periodMatcher.groupCount() == EXPECTED_GROUP_COUNT) {
                final Month month = Month.valueOf(periodMatcher.group(MONTH_GROUP));
                final int dayOfMonth = Integer.parseInt(periodMatcher.group(DATE_GROUP));
                final int hours = Integer.parseInt(periodMatcher.group(HOUR_GROUP));
                final int minutes = Integer.parseInt(periodMatcher.group(MINUTE_GROUP));
                final MonthDay monthDay = MonthDay.of(month, dayOfMonth);
                return Optional.of(new MonthTime(monthDay, LocalTime.of(hours, minutes)));
            }
        } catch (Exception e) {
            LOGGER.debug("Parsed " + input + " but encountered an exception.", e);
        }
        return Optional.empty();
    }
}
