package uk.ac.ebi.quickgo.rest.search.filter.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfigRetrieval;
import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.RequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Optional;
import java.util.function.Function;

import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType.*;

/**
 * Created by edd on 05/06/2016.
 */
@Component
public class RequestConverterFactory {

    private final RequestFilterConfigRetrieval requestConfigRetrieval;

    @Autowired
    public RequestConverterFactory(RequestFilterConfigRetrieval globalRequestFilterConfigRetrieval) {
        this.requestConfigRetrieval = globalRequestFilterConfigRetrieval;
    }

    public QuickGOQuery convertREST(RESTCommRequestFilter requestFilter) {
        Optional<RequestFilterConfig> requestConfigOpt =
                requestConfigRetrieval.getSignature(requestFilter.getSignature());
        return convert(requestConfigOpt, requestFilter, RESTRequestConverter::new, REST_COMM);
    }

    public QuickGOQuery convertJoin(SimpleRequestFilter requestFilter) {
        Optional<RequestFilterConfig> requestConfigOpt =
                requestConfigRetrieval.getSignature(requestFilter.getSignature());
        return convert(requestConfigOpt, requestFilter, JoinRequestConverter::new, JOIN);
    }

    public QuickGOQuery convertSimple(SimpleRequestFilter requestFilter) {
        Optional<RequestFilterConfig> requestConfigOpt =
                requestConfigRetrieval.getSignature(requestFilter.getSignature());
        return convert(requestConfigOpt, requestFilter, SimpleRequestConverter::new, SIMPLE);
    }

    private <T extends RequestFilter> QuickGOQuery convert(Optional<RequestFilterConfig> requestConfigOpt, T requestFilter, Function<RequestFilterConfig, Function<T, QuickGOQuery>> converterCreation, RequestFilterConfig.ExecutionType expectedType) {
        return requestConfigOpt
                .filter(config -> config.getExecution() == expectedType)
                .map(converterCreation)
                .map(converter -> converter.apply(requestFilter))
                .orElseThrow(() -> new IllegalStateException(
                        "Expected RequestFilterConfig to have ExecutionType, " + expectedType +
                                ", for request filter signature: " + requestFilter.getSignature()));
    }
}
