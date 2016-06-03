package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import static uk.ac.ebi.quickgo.rest.search.filter.JoinFilterConverter.createJoinConverterUsingMap;
import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType.REST_COMM;

/**
 * Implementation of the {@link FilterConverterFactory} interface.
 *
 * This class knows which implementation of the {@link FilterConverter} is needed to convert a {@link RequestFilterOld}
 * into a {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
//@Component
public class FilterConverterFactoryImpl implements FilterConverterFactory {
    private final RequestFilterConfigRetrieval globalRequestFilterConfigRetrieval;

    @Autowired
    public FilterConverterFactoryImpl(RequestFilterConfigRetrieval globalRequestFilterConfigRetrieval) {
        this.globalRequestFilterConfigRetrieval = globalRequestFilterConfigRetrieval;
    }

    @Override public FilterConverter createConverter(RequestFilterOld requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "RequestFilter cannot be null");

        // requestFilter.signature identifies the execution config
        // (requestFilter, execution config) pair identifies FilterConverter

        Optional<RequestFilterConfig> fieldOpt = globalRequestFilterConfigRetrieval.getSignature(requestFilter.getField());

        return fieldOpt
                .map(field -> createConverter(requestFilter, field))
                .orElseThrow(() -> new IllegalArgumentException("Unable to process request filter: " + requestFilter));
    }

    /**
     * Creates the appropriate {@link FilterConverter} for the given {@link RequestFilterOld} based on the
     * processing
     * information of the provided by the {@link RequestFilterConfigRetrieval}.
     *
     * @param field holds details about the search field and how to execute it
     * @param filter the filter that will be converted
     * @return a {@link FilterConverter} capable of processing the {@param filter}
     */

    private FilterConverter createConverter(RequestFilterOld filter, RequestFilterConfig field) {
        FilterConverter filterConverter;

        // FilterConverterFactoryX factory = null;
        switch (field.getExecution()) {
            case SIMPLE:
                // factory = SimpleFilterFactory
                filterConverter = new SimpleFilterConverter(filter);
                break;
            case JOIN:
                filterConverter = createJoinConverterUsingMap(
                        field.getProperties(),
                        new SimpleFilterConverter(filter));
                break;
            case REST_COMM:
                throw new RuntimeException(REST_COMM + ": Functionality not Implemented yet");
            default:
                throw new RuntimeException("Unrecognized ExecutionType: " + field.getExecution());
        }
        // factory.

        return filterConverter;
    }

    @Override public uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverter createConverter(SimpleRequestFilter requestFilter) {
        return null;
    }

    @Override public uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverter createConverter(RESTCommRequestFilter requestFilter) {
//        Preconditions.checkArgument(requestFilter != null, "RequestFilter cannot be null");
//        Optional<FieldExecutionConfig> optionalFilterExecutionConfig = globalFilterExecutionConfig.getSignature(requestFilter.getSignature());
//        // check it's a REST_COMM execution type
//
//        RESTCommFilterConverter restCommFilterConverter = new RESTCommFilterConverter();
//
//        // populate converter with stub of url info
//        restCommFilterConverter.setRESTFetcher(optionalFilterExecutionConfig.get().getProperties());
//
//        // populate converter with request specific info
//        requestFilter.configure(restCommFilterConverter);
//
//        return restCommFilterConverter;
        return null;
    }
}