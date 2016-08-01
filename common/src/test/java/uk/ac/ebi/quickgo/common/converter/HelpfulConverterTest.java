package uk.ac.ebi.quickgo.common.converter;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tony Wardell
 * Date: 28/07/2016
 * Time: 13:55
 * Created with IntelliJ IDEA.
 */
public class HelpfulConverterTest {

    @Test
    public void makeArrayOfStringsIntoSingleCSVString(){
        assertThat(HelpfulConverter.toCSV(new String[]{"AAA","BBB","CCC"}), is("AAA,BBB,CCC"));
    }
}
