package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfigRetrieval;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class converts {@link ClientRequest} instances to representational {@link QuickGOQuery} instances.
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

    public QuickGOQuery convert(ClientRequest request) {
        Optional<RequestConfig> configOpt = requestConfigRetrieval.getBySignature(request.getSignature());
        if (configOpt.isPresent()) {
            RequestConfig requestConfig = configOpt.get();
            switch (requestConfig.getExecution()) {
                case REST_COMM:
                    return new RESTRequestConverter(requestConfig).transform(request);
                case SIMPLE:
                    return new SimpleRequestConverter(requestConfig).transform(request);
                case JOIN:
                    return new JoinRequestConverter(requestConfig).transform(request);
                default:
                    throw new IllegalStateException(
                            "RequestConfig execution has not been handled " +
                                    "for signature (" + request.getSignature() + ") in " + requestConfigRetrieval);
            }

        } else {
            throw new IllegalStateException(
                    "Could not find signature (" + request.getSignature() + ") in " + requestConfigRetrieval);
        }
    }
}
