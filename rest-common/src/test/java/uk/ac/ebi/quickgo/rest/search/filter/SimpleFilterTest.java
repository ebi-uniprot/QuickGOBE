package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static uk.ac.ebi.quickgo.rest.TestUtil.convertToCollection;

/**
 * Tests the behaviour of the {@link SimpleFilter} class.
 *
 * @author Tony Wardell
 * Date: 12/05/2016
 * Time: 14:02
 */
public class SimpleFilterTest {
    private static String FILTER_FIELD = "AssignedBy";
    private static String SINGLE_VALUE = "AAA";
    private static String[] MULTIPLE_VALUES = {"AAA", "BBB"};

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SimpleFilter filter;

    @Test
    public void throwsExceptionWhenFieldIsNull() {
        String field = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter field cannot be null or empty");

        filter = new SimpleFilter(field, MULTIPLE_VALUES);
    }

    @Test
    public void throwsExceptionWhenFieldIsEmpty() {
        String field = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter field cannot be null or empty");

        filter = new SimpleFilter(field, MULTIPLE_VALUES);
    }

    @Test
    public void throwsExceptionWhenValueArrayIsNull() {
        String[] values = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter values cannot be null or empty");

        filter = new SimpleFilter(FILTER_FIELD, values);
    }

    @Test
    public void throwsExceptionWhenValueArrayIsEmpty() {
        String[] values = {};

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Filter values cannot be null or empty");

        filter = new SimpleFilter(FILTER_FIELD, values);
    }

    @Test
    public void fieldAndSingleValueCreateAFilter() {
        filter = new SimpleFilter(FILTER_FIELD, SINGLE_VALUE);

        assertThat(filter.getField(), is(FILTER_FIELD));
        assertThat(convertToCollection(filter.getValues()), contains(SINGLE_VALUE));
    }

    @Test
    public void fieldAndMultipleValuesCreateAFilter() {
        filter = new SimpleFilter(FILTER_FIELD, MULTIPLE_VALUES);

        assertThat(filter.getField(), is(FILTER_FIELD));
        assertThat(convertToCollection(filter.getValues()), contains(MULTIPLE_VALUES));
    }

    @Test
    public void transformsFilterWithSingleValueIntoAQuickGoQuery() {
        filter = new SimpleFilter(FILTER_FIELD, SINGLE_VALUE);

        QuickGOQuery query = filter.transform();

        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(FILTER_FIELD, SINGLE_VALUE);

        assertThat(query, is(expectedQuery));
    }

    @Test
    public void transformsFilterWithMultipleValuesIntoAQuickGoQuery() {
        filter = new SimpleFilter(FILTER_FIELD, MULTIPLE_VALUES);

        QuickGOQuery query = filter.transform();

        QuickGOQuery expectedQuery = createORedQuery(FILTER_FIELD, MULTIPLE_VALUES);

        assertThat(query, is(expectedQuery));
    }

    private QuickGOQuery createORedQuery(String field, String... values) {
        return Arrays.stream(values)
                .map(value -> QuickGOQuery.createQuery(field, value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(IllegalArgumentException::new);
    }
}