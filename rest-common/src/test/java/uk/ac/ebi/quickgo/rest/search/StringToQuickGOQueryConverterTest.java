package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    private SearchableField searchableFieldMock;

    @Before
    public void setUp() throws Exception {
        converter = new StringToQuickGOQueryConverter(DEFAULT_FIELD, searchableFieldMock);
    }

    @Test
    public void nullDefaultFieldInTwoParamConstructorThrowsException() throws Exception {
        try {
            new StringToQuickGOQueryConverter(null, searchableFieldMock);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Default search field cannot be null"));
        }
    }

    @Test
    public void nullSearchableFieldInTwoParamConstructorThrowsException() throws Exception {
        try {
            new StringToQuickGOQueryConverter(DEFAULT_FIELD, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Searchable field checker cannot be null"));
        }
    }

    @Test
    public void nullSearchableFieldInSingleParamConstructorThrowsException() throws Exception {
        try {
            new StringToQuickGOQueryConverter(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Searchable field checker cannot be null"));
        }
    }

    @Test
    public void convertFieldAndValueIntoQuery() throws Exception {
        String field = "field1";
        String value = "value1";
        String queryText = concatFieldValueIntoQuery(field, value);

        when(searchableFieldMock.isSearchable(field)).thenReturn(true);

        QuickGOQuery query = converter.convert(queryText);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(field, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithDefaultFieldConvertsValueIntoFieldAndValueQuery() throws Exception {
        String value = "value1";

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(DEFAULT_FIELD, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithoutDefaultFieldConvertsValueIntoValueOnlyQuery() throws Exception {
        converter = new StringToQuickGOQueryConverter(searchableFieldMock);

        String value = "value1";

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createValueQuery(value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithDefaultFieldConvertsValueContainingFieldDelimiterIntoFieldAndValueQuery() throws
                                                                                                       Exception {
        String value = "va:lue1";

        when(searchableFieldMock.isSearchable("va")).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(DEFAULT_FIELD, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    public void converterWithoutDefaultFieldConvertsValueContainingFieldDelimiterIntoValueOnlyQuery() throws
                                                                                                       Exception {
        converter = new StringToQuickGOQueryConverter(searchableFieldMock);

        String value = "va:lue1";

        when(searchableFieldMock.isSearchable("va")).thenReturn(false);

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