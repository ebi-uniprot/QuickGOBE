package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This class checks the behaviour of {@link ExternalServiceResultsTransformer} instances and their interaction with
 * {@link ResponseValueInjector} and {@link RESTFilterConverterFactory} instances.
 *
 * Created 06/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class ExternalServiceResultsTransformerTest {
    private static final String GO_NAME_REQUEST = "goName";
    private static final String TAXON_NAME_REQUEST = "taxonName";
    private static final String GO_ID = "goId";

    @Mock
    private RESTFilterConverterFactory mockRestFetcher;

    @Mock
    private FakeValueInjector mockGoNameInjector;

    @Mock
    private FakeValueInjector mockTaxonNameInjector;

    private ExternalServiceResultsTransformer<FakeResponseModel> resultsTransformer;

    @Before
    public void setUp() {
        when(mockGoNameInjector.getId()).thenReturn(GO_NAME_REQUEST);
        when(mockTaxonNameInjector.getId()).thenReturn(TAXON_NAME_REQUEST);

        resultsTransformer =
                new ExternalServiceResultsTransformer<>(mockRestFetcher, asList(mockGoNameInjector,
                        mockTaxonNameInjector));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRESTFilterConverterFactoryCausesException() {
        new ExternalServiceResultsTransformer<>(null, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldInjectorsCausesException() {
        new ExternalServiceResultsTransformer<>(mockRestFetcher, null);
    }

    @Test
    public void transformingFilterContextWithGoNameRequestResultsInCallToCorrectInjector() {
        FilterContext filterContext = createFilterContext(GO_NAME_REQUEST);

        List<FakeResponseModel> annotations = createMockedAnnotationList(2);
        setValues(annotations, (docNum, ann) -> ann.goId = GO_ID + docNum);

        QueryResult<FakeResponseModel> queryResult = createQueryResult(annotations);
        resultsTransformer.transform(queryResult, filterContext);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(0));
        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(1));
        verify(mockTaxonNameInjector, times(0)).inject(any(), any());
    }

    private QueryResult<FakeResponseModel> createQueryResult(List<FakeResponseModel> annotations) {
        return new QueryResult.Builder<>(annotations.size(), annotations).build();
    }

    @Test
    public void transformingFilterContextWithMultipleRequestsResultsInCallsToCorrectInjector() {
        FilterContext filterContext = createFilterContext(GO_NAME_REQUEST, TAXON_NAME_REQUEST);

        List<FakeResponseModel> annotations = createMockedAnnotationList(2);
        setValues(annotations, (docNum, ann) -> ann.goId = GO_ID + docNum);
        setValues(annotations, (docNum, ann) -> ann.taxonId = docNum);

        QueryResult<FakeResponseModel> queryResult = createQueryResult(annotations);
        resultsTransformer.transform(queryResult, filterContext);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(0));
        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(1));
        verify(mockTaxonNameInjector, times(1)).inject(mockRestFetcher, annotations.get(0));
        verify(mockTaxonNameInjector, times(1)).inject(mockRestFetcher, annotations.get(1));
    }

    @Test
    public void restResponse404LeavesNullAnnotationGoName() {
        FilterContext filterContext = createFilterContext(GO_NAME_REQUEST);

        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());

        List<FakeResponseModel> annotations = createMockedAnnotationList(1);
        assertThat(annotations.get(0).goName, is(nullValue()));

        resultsTransformer.transform(createQueryResult(annotations), filterContext);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(0));
        assertThat(annotations.get(0).goName, is(nullValue()));
    }

    // -------------------- helpers --------------------
    private List<FakeResponseModel> createMockedAnnotationList(int docCount) {
        List<FakeResponseModel> annotations = new ArrayList<>();
        for (int i = 0; i < docCount; i++) {
            annotations.add(new FakeResponseModel());
        }
        return annotations;
    }

    private <T> void setValues(List<T> items, BiConsumer<Integer, T> consumer) {
        for (int i = 0; i < items.size(); i++) {
            consumer.accept(i, items.get(i));
        }
    }

    private FilterContext createFilterContext(String... requests) {
        FilterContext filterContext = new FilterContext();

        ResultTransformationRequests transformationRequests = new ResultTransformationRequests();
        for (String request : requests) {
            transformationRequests.addTransformationRequest(new ResultTransformationRequest(request));
        }

        filterContext.save(ResultTransformationRequests.class, transformationRequests);

        return filterContext;
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
        String goId;
        String goName;
        int taxonId;
        String taxonName;
    }
}