package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
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
    private static final String ISO_8601_DATE_FORMAT = "yyyyMMdd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(ISO_8601_DATE_FORMAT);
    public static final Function<java.util.Date, String> ISO_8601_FORMATTER =
            d -> Objects.isNull(d) ? "" : d.toInstant().atZone(ZoneId.systemDefault()).format(DATE_FORMATTER);

    private DateConverter() {
    }
}
