package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by edd on 05/06/2016.
 */
class SimpleRequestConverter implements Function<SimpleRequest, QuickGOQuery> {

    private final RequestConfig requestConfig;

    SimpleRequestConverter(RequestConfig requestConfig) {
        Preconditions.checkArgument(requestConfig != null, "RequestConfig cannot be null");

        this.requestConfig = requestConfig;
    }

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
