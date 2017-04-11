package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created 06/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologyResultsTransformerTest {
    private static final String GO_NAME_REQUEST = "goName";
    @Mock
    private RESTFilterConverterFactory restFetcher;
    
    @Mock
    private OntologyNameInjector injector;

    @Before
    public void setUp() {
        when(injector.getId()).thenReturn(GO_NAME_REQUEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRESTFilterConverterFactoryCausesException() {
        new OntologyResultsTransformer(null, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldInjectorsCausesException() {
        new OntologyResultsTransformer(restFetcher, null);
    }

    @Test
    public void transformingFilterContextWithGoNameResultsInCallOntologyNameInjector() {
        // todo: tidy method body
        FilterContext filterContext = createFilterContext(new ResultTransformationRequest(GO_NAME_REQUEST));
        
        OntologyResultsTransformer resultsTransformer =
                new OntologyResultsTransformer(restFetcher, singletonList(injector));

        List<Annotation> annotations = new ArrayList<>();
        Annotation annotation1 = new Annotation();
        annotation1.goId = "goId1";
        annotations.add(annotation1);
        Annotation annotation2 = new Annotation();
        annotation2.goId = "goId2";
        annotations.add(annotation2);

        QueryResult<Annotation> queryResult = new QueryResult.Builder<>(2, annotations).build();
        resultsTransformer.transform(queryResult, filterContext);

        verify(injector, times(1)).inject(restFetcher, annotation1);
        verify(injector, times(1)).inject(restFetcher, annotation2);
    }

    private FilterContext createFilterContext(ResultTransformationRequest... requests) {
        FilterContext filterContext = new FilterContext();

        ResultTransformationRequests transformationRequests = new ResultTransformationRequests();
        for (ResultTransformationRequest request : requests) {
            transformationRequests.addTransformationRequest(request);
        }

        filterContext.save(ResultTransformationRequests.class, transformationRequests);

        return filterContext;
    }
}