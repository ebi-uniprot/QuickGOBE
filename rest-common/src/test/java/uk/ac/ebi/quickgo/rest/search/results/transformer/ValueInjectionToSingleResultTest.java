package uk.ac.ebi.quickgo.rest.search.results.transformer;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 09/10/2017
 * Time: 15:54
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class ValueInjectionToSingleResultTest {

    private static final String GO_NAME_REQUEST = "goName";
    private static final String TAXON_NAME_REQUEST = "taxonName";
    private final List<ResponseValueInjector<FakeResponseModel>> requiredInjectors = new ArrayList<>();
    @Mock
    private RESTFilterConverterFactory mockRestFetcher;
    @Mock
    private FakeValueInjector mockGoNameInjector;
    @Mock
    private FakeValueInjector mockTaxonNameInjector;
    private FakeResponseModel result;
    private ValueInjectionToSingleResult<FakeResponseModel> resultMutator;

    @BeforeEach
    void setup() {
        requiredInjectors.add(mockGoNameInjector);
        requiredInjectors.add(mockTaxonNameInjector);
        result = new FakeResponseModel();
        resultMutator = new ValueInjectionToSingleResult<>(mockRestFetcher);
    }

    @Test
    void nullArgumentForRestFilterConverterFactoryToConstructorThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ValueInjectionToSingleResult(null));
    }

    @Test
    void everyInjectorUsedForEveryModel() {
        resultMutator.mutate(result, requiredInjectors);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, result);
        verify(mockTaxonNameInjector, times(1)).inject(mockRestFetcher, result);
    }

    /**
     * Used only for mocking purposes
     */
    private static class FakeValueInjector implements ResponseValueInjector<FakeResponseModel> {
        @Override
        public String getId() {
            return "fake id";
        }

        @Override
        public void inject(RESTFilterConverterFactory restFetcher, FakeResponseModel annotation) {
            // not implemented
        }
    }

    private static class FakeResponseModel {
    }
}
