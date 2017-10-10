package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 09/10/2017
 * Time: 15:54
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class) public class ValueInjectionToSingleResultTest {

    private static final String GO_NAME_REQUEST = "goName";
    private static final String TAXON_NAME_REQUEST = "taxonName";
    private final List<ResponseValueInjector<FakeResponseModel>> requiredInjectors = new ArrayList<>();
    @Mock private RESTFilterConverterFactory mockRestFetcher;
    @Mock private FakeValueInjector mockGoNameInjector;
    @Mock private FakeValueInjector mockTaxonNameInjector;
    private FakeResponseModel result;
    private ValueInjectionToSingleResult<FakeResponseModel> resultMutator;

    @Before public void setup() {

        when(mockGoNameInjector.getId()).thenReturn(GO_NAME_REQUEST);
        when(mockTaxonNameInjector.getId()).thenReturn(TAXON_NAME_REQUEST);
        requiredInjectors.add(mockGoNameInjector);
        requiredInjectors.add(mockTaxonNameInjector);
        result = new FakeResponseModel();
        resultMutator = new ValueInjectionToSingleResult<>(mockRestFetcher);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullArgumentForRestFilterConverterFactoryToConstructorThrowsException() {
        new ValueInjectionToSingleResult(null);
    }

    @Test public void everyInjectorUsedForEveryModel() {
        resultMutator.mutate(result, requiredInjectors);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, result);
        verify(mockTaxonNameInjector, times(1)).inject(mockRestFetcher, result);

    }


    /**
     * Used only for mocking purposes
     */
    private static class FakeValueInjector implements ResponseValueInjector<FakeResponseModel> {
        @Override public String getId() {
            return "fake id";
        }

        @Override public void inject(RESTFilterConverterFactory restFetcher, FakeResponseModel annotation) {
            // not implemented
        }
    }

    private static class FakeResponseModel {
    }
}
