package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.controller.QueryableField;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

/**
 * Utility class to help with the processing of a client request
 */
@Component
public class StringToQuickGOQueryConverter {
    public static final String FIELD_SEPARATOR = ":";

    private static final int SEPARATOR_PRESENT = -1;

    private QueryableField queryableField;
    private String defaultSearchField;

    private StringToQuickGOQueryConverter() {}

    public StringToQuickGOQueryConverter(QueryableField queryableField) {
        Preconditions.checkArgument(queryableField != null, "Queryable field checker can not be null");

        this.queryableField = queryableField;
    }

    public StringToQuickGOQueryConverter(String defaultSearchField, QueryableField queryableField) {
        this(queryableField);

        Preconditions.checkArgument(defaultSearchField != null, "Default search field can not be null");
        this.defaultSearchField = defaultSearchField;
    }

    public QuickGOQuery convert(String queryText) {
        Preconditions.checkArgument(queryText != null && queryText.trim().length() > 0, "Query can not be null");

        return convertToQuery(queryText);
    }

    private QuickGOQuery convertToQuery(String query) {
        QuickGOQuery quickGoQuery;

        int fieldSeparatorPos = query.indexOf(FIELD_SEPARATOR);

        String field;
        String value;

        if (fieldSeparatorPos != SEPARATOR_PRESENT
                && queryableField.isQueryableField(field = query.substring(0, fieldSeparatorPos))) {
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