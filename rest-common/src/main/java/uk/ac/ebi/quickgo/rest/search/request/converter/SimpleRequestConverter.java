package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Defines the conversion of a simple request to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class SimpleRequestConverter implements Function<SimpleRequest, QuickGOQuery> {

    private final RequestConfig requestConfig;

    SimpleRequestConverter(RequestConfig requestConfig) {
        Preconditions.checkArgument(requestConfig != null, "RequestConfig cannot be null");

        this.requestConfig = requestConfig;
    }

    /**
     * Converts a given {@link SimpleRequest} into a corresponding {@link QuickGOQuery}.
     * If {@code simpleRequest} has multiple values, they are ORed together in the
     * resulting query.
     *
     * @param simpleRequest the client request
     * @return a {@link QuickGOQuery} corresponding to a join query, representing the original client request
     */
    @Override
    public QuickGOQuery apply(SimpleRequest simpleRequest) {
        Preconditions.checkArgument(simpleRequest != null, "SimpleRequest cannot be null");

        Stream<String> values = simpleRequest.getValues().stream();

        return values
                .map(value -> QuickGOQuery.createQuery(simpleRequest.getSignature(), value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to create SimpleRequestConverter using: " +
                        simpleRequest + " and " + requestConfig));

    }
}
