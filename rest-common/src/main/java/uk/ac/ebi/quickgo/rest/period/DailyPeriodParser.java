package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 * Turn a string containing a definition for Day And Time into DayTime instance. Strings are required to be in the
 * format DAY(HH:MM)-DAY(HH:MM) which defines a start day and time to an end day and time. An example input string
 * could be <code>MONDAY(09:00)-FRIDAY(17:00)</code>s
 *
 * @author Tony Wardell
 * Date: 11/04/2017
 * Time: 15:26
 * Created with IntelliJ IDEA.
 */
public class DailyPeriodParser implements PeriodParser{
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyPeriodParser.class);
    private static final String DAY_TIME_REGEX = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)\\(" +
            "([0-9]{2}):([0-9]{2})\\)";
    private static final Pattern DAY_TIME_PATTERN = Pattern.compile(DAY_TIME_REGEX);
    private static final int DAY_GROUP = 1;
    private static final int HOUR_GROUP = 2;
    private static final int MINUTE_GROUP = 3;
    private static final int EXPECTED_GROUP_COUNT = 3;
    private static final int REQUIRED_DAYTIME_INSTANCES = 2;
    private static final String TOO_SYMBOL = "-";

    /**
     * Parse a string that contains a day of week and time in the format DAY(HH:MM)-DAY(HH:MM), to produce a Period
     * instance.
     * @param input String
     * @return instance of Optional of Period which is empty if the period could be parsed.
     */
    public Optional<Period> parse(String input) {
        if (Objects.nonNull(input) && !input.isEmpty()) {
            return getPeriod(input);
        }
        return Optional.empty();
    }

    private Optional<Period> getPeriod(String input) {
        String[] dayTimes = input.split(TOO_SYMBOL);
        if (dayTimes.length == REQUIRED_DAYTIME_INSTANCES) {
            List<DayTime> dayTimeList = Arrays.stream(dayTimes)
                                              .map(this::toDayTime)
                                              .filter(Optional::isPresent)
                                              .map(Optional::get)
                                              .collect(toList());
            if (dayTimeList.size() == REQUIRED_DAYTIME_INSTANCES) {
                return Optional.of(new RemainingTimePeriod(dayTimeList.get(0), dayTimeList.get(1)));
            }
        }
        return Optional.empty();
    }

    private Optional<DayTime> toDayTime(String input) {
        try {
            Matcher periodMatcher = DAY_TIME_PATTERN.matcher(input);
            if(periodMatcher.matches() && periodMatcher.groupCount() == EXPECTED_GROUP_COUNT) {
                final int hours = Integer.parseInt(periodMatcher.group(HOUR_GROUP));
                final int minutes = Integer.parseInt(periodMatcher.group(MINUTE_GROUP));
                return Optional.of(new DayTime(DayOfWeek.valueOf(periodMatcher.group(DAY_GROUP)),
                                               LocalTime.of(hours, minutes)));
            }
        } catch (Exception e) {
            LOGGER.info("DailyPeriodParser parsed " + input + " but encountered an exception.", e);
        }
        return Optional.empty();
    }
}
