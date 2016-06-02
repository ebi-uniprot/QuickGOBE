package uk.ac.ebi.quickgo.rest.search.filter;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static uk.ac.ebi.quickgo.rest.search.filter.FieldExecutionConfig.ExecutionType.REST_COMM;
import static uk.ac.ebi.quickgo.rest.search.filter.JoinFilterConverter.createJoinConverterUsingMap;

/**
 * Implementation of the {@link FilterConverterFactory} interface.
 *
 * This class knows which implementation of the {@link FilterConverter} is needed to convert a {@link RequestFilter}
 * into a {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
@Component
public class FilterConverterFactoryImpl implements FilterConverterFactory {
    private final FilterExecutionConfig globalFilterExecutionConfig;

    @Autowired
    public FilterConverterFactoryImpl(FilterExecutionConfig globalFilterExecutionConfig) {
        this.globalFilterExecutionConfig = globalFilterExecutionConfig;
    }

    @Override public FilterConverter createConverter(RequestFilter requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "RequestFilter cannot be null");

        Optional<FieldExecutionConfig> fieldOpt = globalFilterExecutionConfig.getField(requestFilter.getField());

        return fieldOpt.map(field -> createConverter(requestFilter, field))
                .orElseThrow(() -> new IllegalArgumentException("Unable to process request filter: " + requestFilter));
    }

    /**
     * Creates the appropriate {@link FilterConverter} for the given {@link RequestFilter} based on the processing
     * information of the provided by the {@link uk.ac.ebi.quickgo.rest.search.filter.FilterExecutionConfig}.
     *
     * @param field holds details about the search field and how to execute it
     * @param filter the filter that will be converted
     * @return a {@link FilterConverter} capable of processing the {@param filter}
     */
    private FilterConverter createConverter(RequestFilter filter, FieldExecutionConfig field) {
        FilterConverter filterConverter;

        switch (field.getExecution()) {
            case SIMPLE:
                filterConverter = new SimpleFilterConverter(filter);
                break;
            case JOIN:
                filterConverter = createJoinConverterUsingMap(field.getProperties(),
                        new SimpleFilterConverter(filter));
                break;
            case REST_COMM:
                throw new RuntimeException(REST_COMM + ": Functionality not Implemented yet");
            default:
                throw new RuntimeException("Unrecognized ExecutionType: " + field.getExecution());
        }

        return filterConverter;
    }
}