package uk.ac.ebi.quickgo.rest.search.results.transformer;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 09/10/2017
 * Time: 15:54
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class ValueInjectionToQueryResultsTest {
    private static final String GO_NAME_REQUEST = "goName";
    private static final String TAXON_NAME_REQUEST = "taxonName";
    private final List<ResponseValueInjector<FakeResponseModel>> requiredInjectors = new ArrayList<>();
    @Mock
    private RESTFilterConverterFactory mockRestFetcher;
    @Mock
    private FakeValueInjector mockGoNameInjector;
    @Mock
    private FakeValueInjector mockTaxonNameInjector;
    private QueryResult<FakeResponseModel> results;

    @BeforeEach
    void setup() {
        requiredInjectors.add(mockGoNameInjector);
        requiredInjectors.add(mockTaxonNameInjector);
        results = createMockedAnnotationList(2);
    }

    @Test
    void nullArgumentForRestFilterConverterFactoryToConstructorThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ValueInjectionToQueryResults(null));
    }

    @Test
    void everyInjectorUsedForEveryModel() {
        ValueInjectionToQueryResults<FakeResponseModel> resultMutator =
                new ValueInjectionToQueryResults<>(mockRestFetcher);

        resultMutator.mutate(results, requiredInjectors);

        results.getResults().forEach(result -> {
            verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, result);
            verify(mockTaxonNameInjector, times(1)).inject(mockRestFetcher, result);
        });
    }

    @Test
    void emptyModelListDoesNotGetUpdated() {
        ValueInjectionToQueryResults<FakeResponseModel> resultMutator =
                new ValueInjectionToQueryResults<>(mockRestFetcher);
        QueryResult<FakeResponseModel> emptyResults = createMockedAnnotationList(0);

        resultMutator.mutate(emptyResults, requiredInjectors);

        verify(mockGoNameInjector, never()).inject(any(RESTFilterConverterFactory.class), any(FakeResponseModel.class));
        verify(mockTaxonNameInjector, never()).inject(any(RESTFilterConverterFactory.class), any(FakeResponseModel.class));
    }

    // -------------------- helpers --------------------
    private QueryResult<FakeResponseModel> createMockedAnnotationList(int docCount) {
        List<FakeResponseModel> annotations =
                IntStream.range(0, docCount).mapToObj(i -> new FakeResponseModel()).collect(Collectors.toList());

        QueryResult.Builder<FakeResponseModel> builder = new QueryResult.Builder<>(docCount, annotations);
        return builder.build();
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
