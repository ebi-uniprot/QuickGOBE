package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;
import uk.ac.ebi.quickgo.rest.search.request.RESTRequest;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfigRetrieval;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.JOIN;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.REST_COMM;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.SIMPLE;

/**
 * This class provides convenient conversions of {@link ClientRequest} instances to
 * representational {@link QuickGOQuery} instances.
 *
 * Created by Edd on 05/06/2016.
 */
@Component
public class RequestConverterFactory {

    private final RequestConfigRetrieval requestConfigRetrieval;

    @Autowired
    public RequestConverterFactory(RequestConfigRetrieval globalRequestConfigRetrieval) {
        Preconditions.checkArgument(globalRequestConfigRetrieval != null, "RequestConfigRetrieval cannot be null");

        this.requestConfigRetrieval = globalRequestConfigRetrieval;
    }

    /**
     * Converts a client's REST request to a corresponding {@link QuickGOQuery}.
     *
     * @param requestFilter the client's REST request captured as a {@link RESTRequest}
     * @return the corresponding {@link QuickGOQuery}
     */
    public QuickGOQuery convertREST(RESTRequest requestFilter) {
        return convertRequest(requestConfigRetrieval, requestFilter, RESTRequestConverter::new, REST_COMM);
    }

    /**
     * Converts a client's join request to a corresponding {@link QuickGOQuery}.
     *
     * @param requestFilter the client's join request captured as a {@link SimpleRequest}
     * @return the corresponding {@link QuickGOQuery}
     */
    public QuickGOQuery convertJoin(SimpleRequest requestFilter) {
        return convertRequest(requestConfigRetrieval, requestFilter, JoinRequestConverter::new, JOIN);
    }

    /**
     * Converts a client's simple request to a corresponding {@link QuickGOQuery}.
     *
     * @param requestFilter the client's request captured as a {@link SimpleRequest}
     * @return the corresponding {@link QuickGOQuery}
     */
    public QuickGOQuery convertSimple(SimpleRequest requestFilter) {
        return convertRequest(requestConfigRetrieval, requestFilter, SimpleRequestConverter::new, SIMPLE);
    }

    private <T extends ClientRequest> QuickGOQuery convertRequest(
            RequestConfigRetrieval configRetrieval,
            T request,
            Function<RequestConfig, Function<T, QuickGOQuery>> converterCreation,
            RequestConfig.ExecutionType expectedType) {
        Optional<RequestConfig> configOpt = configRetrieval.getSignature(request.getSignature());

        if (configOpt.isPresent()) {
            return configOpt.filter(config -> config.getExecution() == expectedType)
                    .map(converterCreation)
                    .map(converter -> converter.apply(request))
                    .orElseThrow(() -> new IllegalStateException(
                            "Expected RequestConfig to have ExecutionType, " + expectedType +
                                    ", for request filter signature: " + request.getSignature()));
        } else {
            throw new IllegalStateException(
                    "Could not find signature (" + request.getSignature() + ") in " + configRetrieval);
        }
    }
}
