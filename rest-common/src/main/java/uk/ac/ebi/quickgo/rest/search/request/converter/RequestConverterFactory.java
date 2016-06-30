package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class converts {@link FilterRequest} instances to representational {@link QuickGOQuery} instances.
 *
 * Created by Edd on 05/06/2016.
 */
@Component
public class RequestConverterFactory {

    private final FilterConfigRetrieval filterConfigRetrieval;

    @Autowired
    public RequestConverterFactory(FilterConfigRetrieval globalFilterConfigRetrieval) {
        Preconditions.checkArgument(globalFilterConfigRetrieval != null, "RequestConfigRetrieval cannot be null");

        this.filterConfigRetrieval = globalFilterConfigRetrieval;
    }

    public QuickGOQuery convert(FilterRequest request) {
        Optional<FilterConfig> configOpt = filterConfigRetrieval.getBySignature(request.getSignature());
        if (configOpt.isPresent()) {
            FilterConfig filterConfig = configOpt.get();
            switch (filterConfig.getExecution()) {
                case REST_COMM:
                    return new RESTFilterConverter(filterConfig).transform(request);
                case SIMPLE:
                    return new SimpleFilterConverter(filterConfig).transform(request);
                case JOIN:
                    return new JoinFilterConverter(filterConfig).transform(request);
                default:
                    throw new IllegalStateException(
                            "RequestConfig execution has not been handled " +
                                    "for signature (" + request.getSignature() + ") in " + filterConfigRetrieval);
            }

        } else {
            throw new IllegalStateException(
                    "Could not find signature (" + request.getSignature() + ") in " + filterConfigRetrieval);
        }
    }
}
