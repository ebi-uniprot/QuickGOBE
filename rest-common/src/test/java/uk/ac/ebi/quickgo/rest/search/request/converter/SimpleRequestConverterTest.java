package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created 06/06/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleRequestConverterTest {
    private static final String FIELD = "field";
    private static final String FIELD_VALUE_1 = "value1";
    private static final String FIELD_VALUE_2 = "value2";

    @Mock
    private RequestConfig requestConfigMock;
    private SimpleRequestConverter converter;

    @Before
    public void setUp() {
        this.converter = new SimpleRequestConverter(requestConfigMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestConfigForConverterThrowsException() {
        new SimpleRequestConverter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestForConverterThrowsException() {
        converter.apply(null);
    }

    @Test
    public void transformsRequestWithSingleValueIntoAQuickGOQuery() {
        SimpleRequest request = new SimpleRequest(FIELD, singletonList(FIELD_VALUE_1));
        QuickGOQuery resultingQuery = converter.apply(request);
        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(FIELD, FIELD_VALUE_1);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultipleValuesIntoAQuickGOQuery() {
        SimpleRequest request = new SimpleRequest(FIELD, asList(FIELD_VALUE_1, FIELD_VALUE_2));
        QuickGOQuery resultingQuery = converter.apply(request);

        QuickGOQuery expectedQuery =
                QuickGOQuery.createQuery(FIELD, FIELD_VALUE_1).or(QuickGOQuery.createQuery(FIELD, FIELD_VALUE_2));

        assertThat(resultingQuery, is(expectedQuery));
    }

}