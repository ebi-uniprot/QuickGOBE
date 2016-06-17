package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the conversion of a simple request to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class SimpleRequestConverter implements RequestConverter {

    private final RequestConfig requestConfig;

    SimpleRequestConverter(RequestConfig requestConfig) {
        Preconditions.checkArgument(requestConfig != null, "RequestConfig cannot be null");

        this.requestConfig = requestConfig;
    }

    /**
     * Converts a given {@link ClientRequest} into a corresponding {@link QuickGOQuery}.
     * If {@code request} has multiple values, they are ORed together in the
     * resulting query.
     *
     * @param request the client request
     * @return a {@link QuickGOQuery} corresponding to a join query, representing the original client request
     */
    @Override public QuickGOQuery transform(ClientRequest request) {
        Preconditions.checkArgument(request != null, "ClientRequest cannot be null");
        Preconditions.checkArgument(request.getValues().size() == 1,
                "ClientRequest should contain only 1 property for application to a SimpleRequestConverter, " +
                        "instead it contained " + request.getValues().size());

        Stream<String> values = request.getValues().stream().flatMap(Collection::stream);

        return values
                .map(value -> QuickGOQuery
                        .createQuery(request.getSignature().stream().collect(Collectors.joining()), value))
                .reduce(QuickGOQuery::or)
                .orElseThrow(() -> new IllegalStateException("Unable to create SimpleRequestConverter using: " +
                        request + " and " + requestConfig));
    }
}
