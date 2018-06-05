package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple to use implementation to retrieve values from an external restful service.
 */
public class RestValuesRetriever {

    private final RESTFilterConverterFactory converterFactory;

    public RestValuesRetriever(RESTFilterConverterFactory restConverterFactory) {
        this.converterFactory = restConverterFactory;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RestValuesRetriever.class);

    /**
     * Retrieve values identified by the lookup key from an external source.
     * @param lookupKey
     * @param <T> the type of the return value.
     * @return a set of retrieved values.
     */
    public <T> Set<T> retrieveValues(String lookupKey) {
        FilterRequest restRequest = FilterRequest.newBuilder().addProperty(lookupKey).build();

        try {
            ConvertedFilter<List<T>> convertedFilter = converterFactory.convert(restRequest);
            final List<T> convertedValues = convertedFilter.getConvertedValue();
            return new HashSet<>(convertedValues);
        } catch (RetrievalException | IllegalStateException e) {
            LOGGER.error("Failed to retrieve via REST call the relevant Used values: ", e);
        }
        return new HashSet<>();
    }
}
