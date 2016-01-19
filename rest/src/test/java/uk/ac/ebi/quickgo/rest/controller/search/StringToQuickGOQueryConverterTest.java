package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.controller.QueryableField;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link StringToQuickGOQueryConverter} implementation.
 */
@RunWith(MockitoJUnitRunner.class)
public class StringToQuickGOQueryConverterTest {
    private static final String DEFAULT_FIELD = "text";

    private StringToQuickGOQueryConverter converter;

    @Mock
    private QueryableField queryableFieldMock;

    @Before
    public void setUp() throws Exception {
        converter = new StringToQuickGOQueryConverter(DEFAULT_FIELD, queryableFieldMock);
    }

    @Test
    public void nullDefaultFieldInTwoParamConstructorThrowsException() throws Exception {
        try {
            new StringToQuickGOQueryConverter(null, queryableFieldMock);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Default search field can not be null"));
        }
    }

    @Test
    public void nullQueryableFieldInTwoParamConstructorThrowsException() throws Exception {
        try {
            new StringToQuickGOQueryConverter(DEFAULT_FIELD, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Queryable field checker can not be null"));
        }
    }

    @Test
    public void nullQueryableFieldInSingleParamConstructorThrowsException() throws Exception {
        try {
            new StringToQuickGOQueryConverter(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Queryable field checker can not be null"));
        }
    }

    @Test
    public void convertFieldAndValueIntoQuery() throws Exception {
        String field = "field1";
        String value = "value1";
        String queryText = concatFieldValueIntoQuery(field, value);

        when(queryableFieldMock.isQueryableField(field)).thenReturn(true);

        QuickGOQuery query = converter.convert(queryText);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(field, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithDefaultFieldConvertsValueIntoFieldAndValueQuery() throws Exception {
        String value = "value1";

        when(queryableFieldMock.isQueryableField(value)).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(DEFAULT_FIELD, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithoutDefaultFieldConvertsValueIntoValueOnlyQuery() throws Exception {
        converter = new StringToQuickGOQueryConverter(queryableFieldMock);

        String value = "value1";

        when(queryableFieldMock.isQueryableField(value)).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createValueQuery(value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithDefaultFieldConvertsValueContainingFieldDelimiterIntoFieldAndValueQuery() throws
                                                                                                       Exception {
        String value = "va:lue1";

        when(queryableFieldMock.isQueryableField("va")).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(DEFAULT_FIELD, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithoutDefaultFieldConvertsValueContainingFieldDelimiterIntoValueOnlyQuery() throws
                                                                                                       Exception {
        converter = new StringToQuickGOQueryConverter(queryableFieldMock);

        String value = "va:lue1";

        when(queryableFieldMock.isQueryableField("va")).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createValueQuery(value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValueForQueryThrowsException() throws Exception {
        converter.convert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyValueForQueryThrowsException() throws Exception {
        String value = "";

        converter.convert(value);
    }

    private String concatFieldValueIntoQuery(String field, String value) {
        return field + StringToQuickGOQueryConverter.FIELD_SEPARATOR + value;
    }

    private QuickGOQuery createFieldAndValueQuery(String field, String value) {
        return QuickGOQuery.createQuery(field, value);
    }

    private QuickGOQuery createValueQuery(String value) {
        return QuickGOQuery.createQuery(value);
    }
}