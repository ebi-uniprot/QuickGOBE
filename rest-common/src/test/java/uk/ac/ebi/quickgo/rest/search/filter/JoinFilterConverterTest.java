package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the behaviour of the {@link JoinFilterConverter} class.
 */
public class JoinFilterConverterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String FROM_TABLE = "FROM_TABLE";
    private static final String FROM_ATTRIBUTE = "FROM_ATTRIBUTE";
    private static final String TO_TABLE = "TO_TABLE";
    private static final String TO_ATTRIBUTE = "TO_ATTRIBUTE";

    @Test
    public void nullRequestFilterThrowsExceptionInConstructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("RequestFilter can not be null.");

        new JoinFilterConverter(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE, null);
    }

    @Test
    public void converterCreatesTransformedIntoJoinQuickGoQueryWithoutFilter() {
        JoinFilterConverter filterConverter = new JoinFilterConverter(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE,
                TO_ATTRIBUTE);

        QuickGOQuery query = filterConverter.transform();

        QuickGOQuery expectedQuery = QuickGOQuery.createJoinQuery(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE);

        assertThat(query, is(expectedQuery));
    }

    @Test
    public void converterCreatesTransformedIntoJoinQuickGoQueryWithFilter() {
        String field = "fieldX";
        String value = "valueX";

        QuickGOQuery expectedFilter = QuickGOQuery.createQuery(field, value);

        JoinFilterConverter filterConverter = new JoinFilterConverter(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE,
                TO_ATTRIBUTE, expectedFilter);

        QuickGOQuery query = filterConverter.transform();

        QuickGOQuery expectedQuery = QuickGOQuery
                        .createJoinQueryWithFilter(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE, expectedFilter);

        assertThat(query, is(expectedQuery));
    }
}