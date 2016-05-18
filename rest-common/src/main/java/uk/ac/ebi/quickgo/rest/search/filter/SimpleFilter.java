package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * The simplest manifestation of a {@link RequestFilter}. It requires a field name and one or more filter values.
 *
 * The SimpleFilter does not require any extra processing to convert it from its current state into a
 * {@link QuickGOQuery}.
 *
 * @author Tony Wardell
 * Date: 03/05/2016
 * Time: 10:25
 */
class SimpleFilter implements RequestFilter {
    private String field;
    private String[] values;

    SimpleFilter(String field, String... values) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Filter field cannot be null or empty");
        Preconditions.checkArgument(values != null && values.length > 0, "Filter values cannot be null or empty");
        this.field = field;
        this.values = values;
    }

    public String getField() {
        return field;
    }

    public Stream<String> getValues() {
        return Arrays.stream(values);
    }

    @Override public QuickGOQuery transform() {
        return getValues()
                .map(value -> QuickGOQuery.createQuery(field, value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to merge filter queries: " + values));
    }
}