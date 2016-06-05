package uk.ac.ebi.quickgo.rest.search.filter.converter;

import com.google.common.base.Preconditions;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by edd on 05/06/2016.
 */
public class SimpleRequestConverter implements Function<SimpleRequest, QuickGOQuery> {

    private final RequestFilterConfig requestConfig;

    public SimpleRequestConverter(RequestFilterConfig requestFilterConfig) {
        this.requestConfig = requestFilterConfig;
    }

    @Override
    public QuickGOQuery apply(SimpleRequest simpleRequest) {
        Preconditions.checkArgument(simpleRequest != null, "SimpleRequestFilter cannot be null");

        Stream<String> values = simpleRequest.getValues().stream();

        return values
                .map(value -> QuickGOQuery.createQuery(simpleRequest.getSignature(), value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to merge filter queries: " + convertTo(values)));

    }

    private String convertTo(Stream<String> values) {
        return values.collect(Collectors.joining(", "));
    }
}
