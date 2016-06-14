package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.filter.JoinFilterConverter.*;

/**
 * Tests the behaviour of the {@link JoinFilterConverter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class JoinFilterConverterTest {
    private static final String FROM_TABLE = "FROM_TABLE";
    private static final String FROM_ATTRIBUTE = "FROM_ATTRIBUTE";
    private static final String TO_TABLE = "TO_TABLE";
    private static final String TO_ATTRIBUTE = "TO_ATTRIBUTE";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private FilterConverter filterConverterMock;

    @Test
    public void nullConverterForFilterThrowsExceptionWhenUsingFactoryMethodWithFilter() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Converter for the filter query cannot be null.");

        createJoinConverterUsingParameters(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE, null);
    }

    @Test
    public void factoryMethodUsingJoinAttributesMapCreatesJoinFilterConverterCorrectly() {
        Map<String, String> joinMap = new HashMap<>();
        joinMap.put(FROM_TABLE_NAME, FROM_TABLE);
        joinMap.put(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE);
        joinMap.put(TO_TABLE_NAME, TO_TABLE);
        joinMap.put(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE);

        QuickGOQuery filterQuery = QuickGOQuery.createQuery("query");
        when(filterConverterMock.transform()).thenReturn(filterQuery);

        QuickGOQuery expectedQuery = QuickGOQuery.createJoinQueryWithFilter(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE,
                TO_ATTRIBUTE, filterQuery);

        FilterConverter filterConverter = createJoinConverterUsingMap(joinMap, filterConverterMock);

        assertThat(filterConverter.transform(), is(expectedQuery));
    }

    @Test
    public void converterCreatesTransformedIntoJoinQuickGoQueryWithoutFilter() {
        JoinFilterConverter filterConverter = createJoinConverterUsingParametersWithoutFilter(FROM_TABLE,
                FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE);

        QuickGOQuery query = filterConverter.transform();

        QuickGOQuery expectedQuery = QuickGOQuery.createJoinQuery(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE);

        assertThat(query, is(expectedQuery));
    }

    @Test
    public void converterCreatesTransformedIntoJoinQuickGoQueryWithFilter() {
        String field = "fieldX";
        String value = "valueX";

        QuickGOQuery expectedFilter = QuickGOQuery.createQuery(field, value);
        when(filterConverterMock.transform()).thenReturn(expectedFilter);

        JoinFilterConverter filterConverter = createJoinConverterUsingParameters(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE,
                TO_ATTRIBUTE, filterConverterMock);

        QuickGOQuery query = filterConverter.transform();

        QuickGOQuery expectedQuery = QuickGOQuery
                .createJoinQueryWithFilter(FROM_TABLE, FROM_ATTRIBUTE, TO_TABLE, TO_ATTRIBUTE, expectedFilter);

        assertThat(query, is(expectedQuery));
    }
}