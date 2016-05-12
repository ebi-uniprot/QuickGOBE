package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Provides tests of the PrototypeFilter class.
 * @author Tony Wardell
 * Date: 12/05/2016
 * Time: 14:02
 * Created with IntelliJ IDEA.
 */
public class PrototypeFilterTest {

    public static String FILTER_FIELD = "AssignedBy";
    public static String POPULATED_LIST = "AAA,BBB,CCC";
    public static Validator<String> DOES_NOTHING_VALIDATOR = (String s)->{};

    @Test
    public void successfulCreationAndValidationOfArgs(){
        PrototypeFilter prototypeFilter = PrototypeFilter.create(FILTER_FIELD, POPULATED_LIST, DOES_NOTHING_VALIDATOR);
        assertThat(prototypeFilter.getFilterField(), is(FILTER_FIELD));

    }
}
