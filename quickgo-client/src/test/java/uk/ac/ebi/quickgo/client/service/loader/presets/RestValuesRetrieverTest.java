package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestValuesRetrieverTest {

    private static final String RETRIEVE_KEY = "BogusKey";
    RestValuesRetriever retriever;

    @Mock RESTFilterConverterFactory converterFactory;

    ConvertedFilter<List<String>> convertedValuesFound;
    ConvertedFilter<List<String>> convertedValuesNotFound;

    @Before
    public void setup() {
        List<String> returnValues = Arrays.asList("Brandish", "Bungalow");
        convertedValuesFound = new ConvertedFilter<>(returnValues);
        convertedValuesNotFound = new ConvertedFilter<>(Collections.emptyList());
        retriever = new RestValuesRetriever(converterFactory);
    }

    @Test
    public void valuesAreRetrieved() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenReturn(convertedValuesFound);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(true));
        assertThat(retrievedValues.get(), hasSize(2));

    }

    @Test
    public void noValueRetrievedButNoExceptionThrown() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenReturn(convertedValuesNotFound);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(true));
        assertThat(retrievedValues.get(), hasSize(0));
    }

    @Test
    public void illegalStateExceptionThrownDuringRetrieval() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenThrow(IllegalStateException.class);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(false));
    }

    @Test
    public void retrievalExceptionThrownDuringRetrieval() {
        when(converterFactory.<List<String>>convert(any(FilterRequest.class))).thenThrow(RetrievalException.class);

        Optional<List<String>> retrievedValues = retriever.retrieveValues(RETRIEVE_KEY);

        assertThat(retrievedValues.isPresent(), is(false));
    }

}