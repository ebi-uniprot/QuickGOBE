package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created 06/06/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleFilterConverterTest {
    private static final String FIELD1 = "field1";
    private static final String FIELD2 = "field2";
    private static final String FIELD_VALUE_1 = "value1";
    private static final String FIELD_VALUE_2 = "value2";

    @Mock
    private RequestConfig requestConfigMock;
    private SimpleFilterConverter converter;

    @Before
    public void setUp() {
        this.converter = new SimpleFilterConverter(requestConfigMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestConfigForConverterThrowsException() {
        new SimpleFilterConverter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestForConverterThrowsException() {
        converter.transform(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestWithMultiplePropertiesThrowsException() {
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(FIELD1, FIELD_VALUE_1)
                .addProperty(FIELD2, FIELD_VALUE_2)
                .build();
        converter.transform(request);
    }

    @Test
    public void transformsRequestWithSingleValueIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(FIELD1, FIELD_VALUE_1).build();
        QuickGOQuery resultingQuery = converter.transform(request);
        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultipleValuesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(FIELD1, FIELD_VALUE_1, FIELD_VALUE_2).build();
        QuickGOQuery resultingQuery = converter.transform(request);

        QuickGOQuery expectedQuery =
                QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1).or(QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_2));

        assertThat(resultingQuery, is(expectedQuery));
    }

}