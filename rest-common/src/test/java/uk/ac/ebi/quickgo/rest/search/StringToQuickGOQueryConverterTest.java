package uk.ac.ebi.quickgo.rest.search;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link StringToQuickGOQueryConverter} implementation.
 */
@ExtendWith(MockitoExtension.class)
class StringToQuickGOQueryConverterTest {
    private static final String DEFAULT_FIELD = "text";

    private StringToQuickGOQueryConverter converter;

    @Mock
    private SearchableField searchableFieldMock;

    @BeforeEach
    void setUp()  {
        converter = new StringToQuickGOQueryConverter(DEFAULT_FIELD, searchableFieldMock);
    }

    @Test
    void nullDefaultFieldInTwoParamConstructorThrowsException()  {
        try {
            new StringToQuickGOQueryConverter(null, searchableFieldMock);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Default search field cannot be null"));
        }
    }

    @Test
    void nullSearchableFieldInTwoParamConstructorThrowsException()  {
        try {
            new StringToQuickGOQueryConverter(DEFAULT_FIELD, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Searchable field checker cannot be null"));
        }
    }

    @Test
    void nullSearchableFieldInSingleParamConstructorThrowsException()  {
        try {
            new StringToQuickGOQueryConverter(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Searchable field checker cannot be null"));
        }
    }

    @Test
    void convertFieldAndValueIntoQuery()  {
        String field = "field1";
        String value = "value1";
        String queryText = concatFieldValueIntoQuery(field, value);

        when(searchableFieldMock.isSearchable(field)).thenReturn(true);

        QuickGOQuery query = converter.convert(queryText);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(field, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    void converterWithDefaultFieldConvertsValueIntoFieldAndValueQuery()  {
        String value = "value1";

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(DEFAULT_FIELD, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    void converterWithoutDefaultFieldConvertsValueIntoValueOnlyQuery()  {
        converter = new StringToQuickGOQueryConverter(searchableFieldMock);

        String value = "value1";

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createValueQuery(value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    void converterWithDefaultFieldConvertsValueContainingFieldDelimiterIntoFieldAndValueQuery() {
        String value = "va:lue1";

        when(searchableFieldMock.isSearchable("va")).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createFieldAndValueQuery(DEFAULT_FIELD, value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    void converterWithoutDefaultFieldConvertsValueContainingFieldDelimiterIntoValueOnlyQuery() {
        converter = new StringToQuickGOQueryConverter(searchableFieldMock);

        String value = "va:lue1";

        when(searchableFieldMock.isSearchable("va")).thenReturn(false);

        QuickGOQuery query = converter.convert(value);

        QuickGOQuery expectedQuery = createValueQuery(value);

        assertThat(query, is(equalTo(expectedQuery)));
    }

    @Test
    void nullValueForQueryThrowsException()  {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(null));
    }

    @Test
    void emptyValueForQueryThrowsException()  {
        String value = "";
        assertThrows(IllegalArgumentException.class, () -> converter.convert(value));
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