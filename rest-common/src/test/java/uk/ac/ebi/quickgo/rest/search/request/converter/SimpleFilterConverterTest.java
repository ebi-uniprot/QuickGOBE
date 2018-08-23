package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

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
    private FilterConfig filterConfigMock;
    private SimpleFilterConverter converter;

    @Before
    public void setUp() {
        this.converter = new SimpleFilterConverter(filterConfigMock);
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
    public void requestNoPropertyThrowsException() {
        FilterRequest request = FilterRequest.newBuilder().build();
        converter.transform(request);
    }

    @Test
    public void transformsRequestWithSingleValueIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(FIELD1, FIELD_VALUE_1).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultipleValuesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(FIELD1, FIELD_VALUE_1, FIELD_VALUE_2).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
                or(
                        QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1),
                        QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_2));

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultiplePropertiesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(FIELD1, FIELD_VALUE_1)
                .addProperty(FIELD2, FIELD_VALUE_2)
                .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
                and(
                        QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1),
                        QuickGOQuery.createQuery(FIELD2, FIELD_VALUE_2));

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultiplePropertiesAndMultipleValuesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(FIELD1, FIELD_VALUE_1, FIELD_VALUE_2)
                .addProperty(FIELD2, FIELD_VALUE_2, FIELD_VALUE_1)
                .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
                and(
                        or(
                                QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1),
                                QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_2)
                        ),
                        or(
                                QuickGOQuery.createQuery(FIELD2, FIELD_VALUE_2),
                                QuickGOQuery.createQuery(FIELD2, FIELD_VALUE_1)
                        )
                );

        assertThat(resultingQuery, is(expectedQuery));
    }

}