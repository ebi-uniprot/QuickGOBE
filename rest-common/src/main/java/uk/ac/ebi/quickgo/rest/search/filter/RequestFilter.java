package uk.ac.ebi.quickgo.rest.search.filter;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Data structure that holds the field name and values of a filter requested by a REST service endpoint.
 *
 * @author Tony Wardell
 * Date: 03/05/2016
 * Time: 10:25
 */
public class RequestFilter {
    private String field;
    private String[] values;

    public RequestFilter(String field, String... values) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Filter field cannot be null or empty");
        Preconditions.checkArgument(values != null && values.length > 0, "Filter values cannot be null or empty");

        this.field = field;
        this.values = values;
    }

    /**
     * Provides the field (column) the filter will be applied to.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Provides the field (column) the filter will be applied to.
     *
     * @return the field
     */
    public Stream<String> getValues() {
        return Arrays.stream(values);
    }
}