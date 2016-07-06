package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the conversion of a simple request to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class SimpleFilterConverter implements FilterConverter {

    private final FilterConfig filterConfig;

    SimpleFilterConverter(FilterConfig filterConfig) {
        Preconditions.checkArgument(filterConfig != null, "FilterConfig cannot be null");

        this.filterConfig = filterConfig;
    }

    /**
     * Converts a given {@link FilterRequest} into a corresponding {@link QuickGOQuery}.
     * If {@code request} has multiple values, they are ORed together in the
     * resulting query.
     *
     * @param request the client request
     * @return a {@link QuickGOQuery} corresponding to a join query, representing the original client request
     */
    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");
        Preconditions.checkArgument(request.getValues().size() == 1,
                "FilterRequest should contain only 1 property for application to a SimpleRequestConverter, " +
                        "instead it contained " + request.getValues().size());

        Stream<String> values = request.getValues().stream().flatMap(Collection::stream);

        return values
                .map(value -> QuickGOQuery
                        .createQuery(request.getSignature().stream().collect(Collectors.joining()), value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to create SimpleRequestConverter using: " +
                        request + " and " + filterConfig));
    }
}
