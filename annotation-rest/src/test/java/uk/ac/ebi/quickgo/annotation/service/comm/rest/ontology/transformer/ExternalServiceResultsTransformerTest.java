package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;

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
 * This class checks 
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

    private ExternalServiceResultsTransformer resultsTransformer;

    @Before
    public void setUp() {
        when(mockGoNameInjector.getId()).thenReturn(GO_NAME_REQUEST);
        when(mockTaxonNameInjector.getId()).thenReturn(TAXON_NAME_REQUEST);

        resultsTransformer =
                new ExternalServiceResultsTransformer(mockRestFetcher, asList(mockGoNameInjector, mockTaxonNameInjector));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRESTFilterConverterFactoryCausesException() {
        new ExternalServiceResultsTransformer(null, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldInjectorsCausesException() {
        new ExternalServiceResultsTransformer(mockRestFetcher, null);
    }

    @Test
    public void transformingFilterContextWithGoNameRequestInCallToCorrectInjector() {
        FilterContext filterContext = createFilterContext(GO_NAME_REQUEST);

        List<Annotation> annotations = createMockedAnnotationList(2);
        setValues(annotations, (docNum, ann) -> ann.goId = GO_ID + docNum);

        QueryResult<Annotation> queryResult = createQueryResult(annotations);
        resultsTransformer.transform(queryResult, filterContext);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(0));
        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(1));
        verify(mockTaxonNameInjector, times(0)).inject(any(), any());
    }

    private QueryResult<Annotation> createQueryResult(List<Annotation> annotations) {
        return new QueryResult.Builder<>(annotations.size(), annotations).build();
    }

    @Test
    public void transformingFilterContextWithMultipleRequestsResultsInCallsToCorrectInjector() {
        FilterContext filterContext = createFilterContext(GO_NAME_REQUEST, TAXON_NAME_REQUEST);
        
        List<Annotation> annotations = createMockedAnnotationList(2);
        setValues(annotations, (docNum, ann) -> ann.goId = GO_ID + docNum);
        setValues(annotations, (docNum, ann) -> ann.taxonId = docNum);

        QueryResult<Annotation> queryResult = createQueryResult(annotations);
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

        List<Annotation> annotations = createMockedAnnotationList(1);
        assertThat(annotations.get(0).goName, is(nullValue()));

        resultsTransformer.transform(createQueryResult(annotations), filterContext);

        verify(mockGoNameInjector, times(1)).inject(mockRestFetcher, annotations.get(0));
        assertThat(annotations.get(0).goName, is(nullValue()));
    }

    // -------------------- helpers --------------------
    private List<Annotation> createMockedAnnotationList(int docCount) {
        List<Annotation> annotations = new ArrayList<>();
        for (int i = 0; i < docCount; i++) {
            annotations.add(new Annotation());
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
    private static class FakeValueInjector implements ResponseValueInjector {
        @Override public String getId() {
            return "fake id";
        }

        @Override public void inject(RESTFilterConverterFactory restFetcher, Annotation annotation) {
            // not implemented
        }
    }
}