package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Simple to use implementation to retrieve values from an external restful service.
 */
public class RestValuesRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestValuesRetriever.class);
    private final RESTFilterConverterFactory converterFactory;

    public RestValuesRetriever(RESTFilterConverterFactory restConverterFactory) {
        this.converterFactory = restConverterFactory;
    }

    /**
     * Retrieve values identified by the lookup key from an external source.
     * @param <T> the type of the return value.
     * @param lookupKey the value used as the identifier for the looked up values.
     * @return a set of retrieved values.
     */
    public <T> Optional<T> retrieveValues(String lookupKey) {
        checkArgument(Objects.nonNull(lookupKey), "The lookupKey passed to the RestValuesRetriever is " + "null");
        FilterRequest restRequest = FilterRequest.newBuilder().addProperty(lookupKey).build();
        try {
            ConvertedFilter<T> convertedFilter = converterFactory.convert(restRequest);
            return Optional.of(convertedFilter.getConvertedValue());
        } catch (RetrievalException | IllegalStateException e) {
            LOGGER.error(String.format("Failed to retrieve values for %s via REST", lookupKey), e);
        }
        return Optional.empty();
    }
}
