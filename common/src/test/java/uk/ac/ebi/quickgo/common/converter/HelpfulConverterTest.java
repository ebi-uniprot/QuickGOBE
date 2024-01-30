package uk.ac.ebi.quickgo.common.converter;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;

/**
 * @author Tony Wardell
 * Date: 28/07/2016
 * Time: 13:55
 * Created with IntelliJ IDEA.
 */
class HelpfulConverterTest {

    @Test
    void makeArrayOfStringsIntoSingleCSVString(){
        assertThat(toCSV("AAA", "BBB", "CCC"), is("AAA,BBB,CCC"));
    }

    @Test
    void canConvertNullListToEmptyCSV() {
        assertThat(toCSV((List<String>) null), is(""));
    }
}
