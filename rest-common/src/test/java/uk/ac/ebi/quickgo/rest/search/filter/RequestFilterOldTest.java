package uk.ac.ebi.quickgo.rest.search.filter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static uk.ac.ebi.quickgo.rest.TestUtil.convertToCollection;

/**
 * Tests the behaviour of the {@link RequestFilterOld} class.
 *
 * @author Tony Wardell
 * Date: 12/05/2016
 * Time: 14:02
 */
public class RequestFilterOldTest {
    private static String FILTER_FIELD = "AssignedBy";
    private static String SINGLE_VALUE = "AAA";
    private static String[] MULTIPLE_VALUES = {"AAA", "BBB"};

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RequestFilterOld filter;

    @Test
    public void throwsExceptionWhenFieldIsNull() {
        String field = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter field cannot be null or empty");

        filter = new RequestFilterOld(field, MULTIPLE_VALUES);
    }

    @Test
    public void throwsExceptionWhenFieldIsEmpty() {
        String field = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter field cannot be null or empty");

        filter = new RequestFilterOld(field, MULTIPLE_VALUES);
    }

    @Test
    public void throwsExceptionWhenValueArrayIsNull() {
        String[] values = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter values cannot be null or empty");

        filter = new RequestFilterOld(FILTER_FIELD, values);
    }

    @Test
    public void throwsExceptionWhenValueArrayIsEmpty() {
        String[] values = {};

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter values cannot be null or empty");

        filter = new RequestFilterOld(FILTER_FIELD, values);
    }

    @Test
    public void fieldAndSingleValueCreateAFilter() {
        filter = new RequestFilterOld(FILTER_FIELD, SINGLE_VALUE);

        assertThat(filter.getField(), is(FILTER_FIELD));
        assertThat(convertToCollection(filter.getValues()), contains(SINGLE_VALUE));
    }

    @Test
    public void fieldAndMultipleValuesCreateAFilter() {
        filter = new RequestFilterOld(FILTER_FIELD, MULTIPLE_VALUES);

        assertThat(filter.getField(), is(FILTER_FIELD));
        assertThat(convertToCollection(filter.getValues()), contains(MULTIPLE_VALUES));
    }
}