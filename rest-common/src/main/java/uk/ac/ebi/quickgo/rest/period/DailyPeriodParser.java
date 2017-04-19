package uk.ac.ebi.quickgo.rest.period;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DailyPeriodParser {
    private static final String DAY_TIME_REGEX = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)\\(" +
            "([0-9]{2}):([0-9]{2})\\)";
    private static final Pattern DAY_TIME_PATTERN = Pattern.compile(DAY_TIME_REGEX);
    private static final int DAY_GROUP = 1;
    private static final int HOUR_GROUP = 2;
    private static final int MINUTE_GROUP = 3;
    private static final int EXPECTED_GROUP_COUNT = 3;
    private static final int REQUIRED_DAYTIME_INSTANCES = 2;

    /**
     * Parse a string that contains a day of week and time in the format DAY(HH:MM)-DAY(HH:MM), to produce an
     * instance of CachingPeriodAllowed.
     * @param input String
     * @return instance of Period or null if no valid period could be parsed.
     */
    public Period parse(String input) {
        if (!Objects.nonNull(input) || input.isEmpty()) {
            return new ZeroDurationPeriod();
        }

        String[] dayTimes = input.split("-");
        if(dayTimes.length == REQUIRED_DAYTIME_INSTANCES) {
            List<DayTime> dayTimeList = Arrays.stream(dayTimes)
                                              .map(this::toDayTime)
                                              .filter(Objects::nonNull)
                                              .collect(toList());
            if (dayTimeList.size() == REQUIRED_DAYTIME_INSTANCES) {
                return new ReducingDailyPeriod(dayTimeList.get(0), dayTimeList.get(1));
            }
        }
        return null;
    }

    private DayTime toDayTime(String dayTime) {
        Matcher periodMatcher = DAY_TIME_PATTERN.matcher(dayTime);
        if(periodMatcher.matches() && periodMatcher.groupCount() == EXPECTED_GROUP_COUNT) {
            final int hours = Integer.parseInt(periodMatcher.group(HOUR_GROUP));
            final int minutes = Integer.parseInt(periodMatcher.group(MINUTE_GROUP));
            return new DayTime(DayOfWeek.valueOf(periodMatcher.group(DAY_GROUP)), LocalTime.of(hours, minutes));
        }
        return null;
    }

}
