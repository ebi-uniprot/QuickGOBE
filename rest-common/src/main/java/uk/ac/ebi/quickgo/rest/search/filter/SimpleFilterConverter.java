package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class knows how to convert instances of {@link RequestFilterOld} that do not require extra processing into
 * {@link QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
class SimpleFilterConverter implements FilterConverter {
    private final RequestFilterOld filter;

    SimpleFilterConverter(RequestFilterOld filter) {
        Preconditions.checkArgument(filter != null, "RequestFilter cannot be null.");

        this.filter = filter;
    }

    @Override public QuickGOQuery transform() {
        Stream<String> values = filter.getValues();

        return values
                .map(value -> QuickGOQuery.createQuery(filter.getField(), value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to merge filter queries: " + convertTo(values)));
    }

    private String convertTo(Stream<String> values) {
        return values.collect(Collectors.joining(","));
    }
}