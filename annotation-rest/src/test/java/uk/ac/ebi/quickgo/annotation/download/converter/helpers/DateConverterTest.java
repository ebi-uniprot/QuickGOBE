package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.Date;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DateConverterTest {

    @Test
    public void dateFormatted() {
        Date date = new Date();
        date.setTime(0);

        String formattedDate = DateConverter.ISO_8601_FORMATTER.apply(date);

        assertThat(formattedDate, is("19700101"));
    }

    @Test(expected = NullPointerException.class)
    public void nullDate() {

        DateConverter.ISO_8601_FORMATTER.apply(null);

    }

}