package uk.ac.ebi.quickgo.common.search;

import uk.ac.ebi.quickgo.common.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;

/**
 * Utility class to help with the processing of a client request
 */
public class StringToQuickGOQueryConverter {
    public static final String FIELD_SEPARATOR = ":";

    private static final int SEPARATOR_PRESENT = -1;

    private SearchableField searchableField;
    private String defaultSearchField;

    private StringToQuickGOQueryConverter() {}

    public StringToQuickGOQueryConverter(SearchableField searchableField) {
        Preconditions.checkArgument(searchableField != null, "Searchable field checker cannot be null");

        this.searchableField = searchableField;
    }

    public StringToQuickGOQueryConverter(String defaultSearchField, SearchableField searchableField) {
        this(searchableField);

        Preconditions.checkArgument(defaultSearchField != null, "Default search field cannot be null");
        this.defaultSearchField = defaultSearchField;
    }

    public QuickGOQuery convert(String queryText) {
        Preconditions.checkArgument(queryText != null && queryText.trim().length() > 0, "Query cannot be null");

        return convertToQuery(queryText);
    }

    private QuickGOQuery convertToQuery(String query) {
        QuickGOQuery quickGoQuery;

        int fieldSeparatorPos = query.indexOf(FIELD_SEPARATOR);

        String field;
        String value;

        if (fieldSeparatorPos != SEPARATOR_PRESENT
                && searchableField.isSearchable(field = query.substring(0, fieldSeparatorPos))) {
            value = query.substring(fieldSeparatorPos + 1, query.length());

            quickGoQuery = QuickGOQuery.createQuery(field, value);
        } else {
            value = query;

            if(defaultSearchField != null) {
                quickGoQuery = QuickGOQuery.createQuery(defaultSearchField, value);
            } else {
                quickGoQuery = QuickGOQuery.createQuery(value);
            }
        }

        return quickGoQuery;
    }
}