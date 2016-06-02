package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Arrays;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link SimpleFilterConverter} class.
 */
public class SimpleFilterConverterTest {
    private static String FILTER_FIELD = "AssignedBy";
    private static String SINGLE_VALUE = "AAA";
    private static String[] MULTIPLE_VALUES = {"AAA", "BBB"};

    @Test
    public void transformsFilterWithSingleValueIntoAQuickGoQuery() {
        RequestFilterOld filter = new RequestFilterOld(FILTER_FIELD, SINGLE_VALUE);

        SimpleFilterConverter converter = new SimpleFilterConverter(filter);

        QuickGOQuery query = converter.transform();

        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(FILTER_FIELD, SINGLE_VALUE);

        assertThat(query, is(expectedQuery));
    }

    @Test
    public void transformsFilterWithMultipleValuesIntoAQuickGoQuery() {
        RequestFilterOld filter = new RequestFilterOld(FILTER_FIELD, MULTIPLE_VALUES);

        SimpleFilterConverter converter = new SimpleFilterConverter(filter);

        QuickGOQuery query = converter.transform();

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