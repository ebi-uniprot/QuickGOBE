package uk.ac.ebi.quickgo.rest.search.filter.converter;

import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfigRetrieval;
import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.RequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType.REST_COMM;
import static uk.ac.ebi.quickgo.rest.search.filter.converter.JoinFilterConverter.createJoinConverterUsingMap;

/**
 * Implementation of the {@link FilterConverterFactory} interface.
 *
 * This class
 * This class knows which implementation of the {@link FilterConverter} is
 * needed to convert a {@link RequestFilter} into a {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
@Component
public class FilterConverterFactoryImpl implements FilterConverterFactory {
    private final RequestFilterConfigRetrieval globalRequestFilterConfigRetrieval;

    @Autowired
    public FilterConverterFactoryImpl(RequestFilterConfigRetrieval globalRequestFilterConfigRetrieval) {
        this.globalRequestFilterConfigRetrieval = globalRequestFilterConfigRetrieval;
    }

    @Override public Optional<FilterConverter> simpleConverter(SimpleRequestFilter requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "SimpleRequestFilter cannot be null");
        Optional<RequestFilterConfig> fieldOpt =
                globalRequestFilterConfigRetrieval.getSignature(requestFilter.getSignature());

        return fieldOpt.map(filterExecutionConfig -> {
            FilterConverter filterConverter = null;
            switch (filterExecutionConfig.getExecution()) {
                case SIMPLE:
                    filterConverter = new SimpleFilterConverter(requestFilter);
                    break;
                case JOIN:
                    filterConverter = createJoinConverterUsingMap(
                            filterExecutionConfig.getProperties(),
                            new SimpleFilterConverter(requestFilter));
                    break;
                default:
                    throw new IllegalStateException(
                            "Cannot create FilterConverter for SimpleRequestFilter & ExecutionType, "
                                    + filterExecutionConfig.getExecution());
            }
            return filterConverter;
        });
    }

    @Override public Optional<RESTCommFilterConverter> restConverter(RESTCommRequestFilter requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "RESTCommRequestFilter cannot be null");
        Optional<RequestFilterConfig> filterConfigOpt =
                globalRequestFilterConfigRetrieval.getSignature(requestFilter.getSignature());

        return filterConfigOpt
                .map(config -> {
                    if (config.getExecution() == REST_COMM) {
                        RESTCommFilterConverter restConverter = new RESTCommFilterConverter();
                        restConverter.setRESTFetcher(config.getProperties());
                        requestFilter.configure(restConverter);
                        return restConverter;
                    } else {
                        throw new IllegalStateException(
                                "Expected RequestFilterConfig to have ExecutionType, " + REST_COMM +
                                ", for request filter signature: " + requestFilter.getSignature());
                    }
                });
    }

}