package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.controller.FilterRequestConfig;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

/**
 * <p>A generic factory to be used for performing arbitrary REST calls, whose details (including
 * address) are specified in a {@link FilterRequest}, and subsequently retrieving
 * instantiated instances that represent the response.
 *
 * <p>This class differs from {@link FilterConverterFactory} in that it can return any instance/response type,
 * whereas {@link FilterConverterFactory} returns specifically {@link QuickGOQuery} instances.
 *
 * Created 31/08/16
 * @author Edd
 */
@Component
@Import(FilterRequestConfig.class)
public class RESTFilterConverterFactory {
    private final FilterConfigRetrieval filterConfigRetrieval;
    private final RestOperations restOperations;

    public RESTFilterConverterFactory(
            FilterConfigRetrieval globalFilterConfigRetrieval,
            RestOperations restOperations) {
        Preconditions.checkArgument(globalFilterConfigRetrieval != null, "RequestConfigRetrieval cannot be null");
        Preconditions.checkArgument(restOperations != null, "RestOperations cannot be null");

        this.filterConfigRetrieval = globalFilterConfigRetrieval;
        this.restOperations = restOperations;
    }

    public <T> ConvertedFilter<T> convert(FilterRequest request) {
        Optional<FilterConfig> configOpt = filterConfigRetrieval.getBySignature(request.getSignature());
        if (configOpt.isPresent()) {
            FilterConfig filterConfig = configOpt.get();
            switch (filterConfig.getExecution()) {
                case REST_COMM:
                    return new RESTFilterConverter<T>(filterConfig, restOperations).transform(request);
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

    @Override public String toString() {
        return "RESTFilterConverterFactory{" +
                "filterConfigRetrieval=" + filterConfigRetrieval +
                ", restOperations=" + restOperations +
                '}';
    }
}
