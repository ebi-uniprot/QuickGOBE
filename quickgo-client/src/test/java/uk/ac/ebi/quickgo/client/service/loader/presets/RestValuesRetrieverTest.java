package uk.ac.ebi.quickgo.client.service.loader.presets;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestValuesRetrieverTest {

    private static final String RETRIEVE_KEY = "BogusKey";
    private RestValuesRetriever retriever;

    @Mock private RESTFilterConverterFactory converterFactory;

    private ConvertedFilter<List<String>> convertedValuesFound;
    private ConvertedFilter<List<String>> convertedValuesNotFound;

    @BeforeEach
    void setup() {
        List<String> returnValues = Arrays.asList("Brandish", "Bungalow");
        convertedValuesFound = new ConvertedFilter<>(returnValues);
        convertedValuesNotFound = new ConvertedFilter<>(Collections.emptyList());
        retriever = new RestValuesRetriever(converterFactory);
    }

    @Test
    void valuesAreRetrieved() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenReturn(convertedValuesFound);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(true));
        assertThat(retrievedValues.get(), hasSize(2));

    }

    @Test
    void noValueRetrievedButNoExceptionThrown() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenReturn(convertedValuesNotFound);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(true));
        assertThat(retrievedValues.get(), hasSize(0));
    }

    @Test
    void nullLookupKeyResultsInIllegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> retriever.retrieveValues(null));
    }

    @Test
    void illegalStateExceptionThrownDuringRetrieval() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenThrow(IllegalStateException.class);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(false));
    }

    @Test
    void retrievalExceptionThrownDuringRetrieval() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenThrow(RetrievalException.class);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(false));
    }

    @Test
    void anyOtherExceptionsArePastOn() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenThrow(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> retriever.retrieveValues(RETRIEVE_KEY));
    }

}