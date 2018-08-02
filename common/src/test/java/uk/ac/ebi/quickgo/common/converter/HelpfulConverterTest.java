package uk.ac.ebi.quickgo.common.converter;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;

/**
 * @author Tony Wardell
 * Date: 28/07/2016
 * Time: 13:55
 * Created with IntelliJ IDEA.
 */
public class HelpfulConverterTest {

    @Test
    public void makeArrayOfStringsIntoSingleCSVString(){
        assertThat(toCSV("AAA", "BBB", "CCC"), is("AAA,BBB,CCC"));
    }

    @Test
    public void canConvertNullListToEmptyCSV() {
        assertThat(toCSV((List<String>) null), is(""));
    }
}
