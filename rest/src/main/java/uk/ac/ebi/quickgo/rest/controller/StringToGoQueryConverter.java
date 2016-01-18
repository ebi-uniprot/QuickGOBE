package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.repo.solr.query.GoQuery;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

/**
 * Utility class to help with the processing of a client request
 */
@Component
public class StringToGoQueryConverter {
    public static final String FIELD_SEPARATOR = ":";

    private static final int SEPARATOR_PRESENT = -1;

    private QueryableField queryableField;
    private String defaultSearchField;

    private StringToGoQueryConverter() {}

    public StringToGoQueryConverter(QueryableField queryableField) {
        Preconditions.checkArgument(queryableField != null, "Queryable field checker can not be null");

        this.queryableField = queryableField;
    }

    public StringToGoQueryConverter(String defaultSearchField, QueryableField queryableField) {
        this(queryableField);

        Preconditions.checkArgument(defaultSearchField != null, "Default search field can not be null");
        this.defaultSearchField = defaultSearchField;
    }

    public GoQuery convert(String queryText) {
        Preconditions.checkArgument(queryText != null && queryText.trim().length() > 0, "Query can not be null");

        return convertToQuery(queryText);
    }

    private GoQuery convertToQuery(String query) {
        GoQuery goQuery;

        int fieldSeparatorPos = query.indexOf(FIELD_SEPARATOR);

        String field;
        String value;

        if (fieldSeparatorPos != SEPARATOR_PRESENT
                && queryableField.isQueryableField(field = query.substring(0, fieldSeparatorPos))) {
            value = query.substring(fieldSeparatorPos + 1, query.length());

            goQuery = GoQuery.createQuery(field, value);
        } else {
            value = query;

            if(defaultSearchField != null) {
                goQuery = GoQuery.createQuery(defaultSearchField, value);
            } else {
                goQuery = GoQuery.createQuery(value);
            }
        }

        return goQuery;
    }
}