package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;

/**
 * A home for the logic to format dates into Strings.
 *
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 14:54
 * Created with IntelliJ IDEA.

 */
public class DateConverter {

    private DateConverter() {
    }

    private static final DateTimeFormatter YYYYMMDD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static final Function<java.util.Date, String> toYYYYMMDD =
            d -> d.toInstant().atZone(ZoneId.systemDefault()).format(YYYYMMDD_DATE_FORMAT);

    public static String toYearMonthDay(Date date) {
        return toYYYYMMDD.apply(date);
    }

}
