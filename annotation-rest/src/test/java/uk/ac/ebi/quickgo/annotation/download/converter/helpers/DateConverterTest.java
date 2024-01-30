package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.DateConverter.ISO_8601_FORMATTER;

class DateConverterTest {

    @Test
    void dateFormatted() {
        Date date = new Date();
        date.setTime(0);

        String formattedDate = ISO_8601_FORMATTER.apply(date);

        assertThat(formattedDate, is("19700101"));
    }

    @Test
    void nullDate() {
        Date date = null;

        String formattedDate = ISO_8601_FORMATTER.apply(date);

        assertThat(formattedDate, is(""));

    }

}