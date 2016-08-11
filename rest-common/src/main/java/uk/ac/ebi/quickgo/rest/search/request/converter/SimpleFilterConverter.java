package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.ConvertedResponse;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.ac.ebi.quickgo.rest.comm.ConvertedResponse.simpleConvertedResponse;

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
    @Override public ConvertedResponse<QuickGOQuery> transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");
        Preconditions.checkArgument(request.getValues().size() == 1,
                "FilterRequest should contain only 1 property for application to a SimpleRequestConverter, " +
                        "instead it contained " + request.getValues().size());

        Stream<String> values = request.getValues().stream().flatMap(Collection::stream);

        return simpleConvertedResponse(getQuickGOQuery(request, values));
    }

    /**
     * Computes the {@link QuickGOQuery} corresponding to for the specified {@link FilterRequest} and {@code values}.
     *
     * <p>Note: inlining this method, as parameter to another method, lead to compilation failure, due to:
     * <ul>
     *     <li>http://stackoverflow.com/questions/25523375/java8-lambdas-and-exceptions</li>
     *     <li>https://bugs.openjdk.java.net/browse/JDK-8054569</li>
     * </ul>
     *
     * @param request the filter request
     * @param values the values, whose combination with the filter details, enable creation of a
     *        corresponding {@link QuickGOQuery}
     * @return the corresponding {@link QuickGOQuery}
     */
    private QuickGOQuery getQuickGOQuery(FilterRequest request, Stream<String> values) {
        return values.map(value -> QuickGOQuery
                .createQuery(request.getSignature().stream().collect(Collectors.joining()), value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to create SimpleRequestConverter using: " +
                        request + " and " + filterConfig));
    }
}
