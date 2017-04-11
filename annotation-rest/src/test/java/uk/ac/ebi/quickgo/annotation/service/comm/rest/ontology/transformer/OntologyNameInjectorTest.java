package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Created 11/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologyNameInjectorTest {

    @Mock
    private RESTFilterConverterFactory mockRestFetcher;

    private OntologyNameInjector nameInjector;

    @Before
    public void setUp() {
        nameInjector = new OntologyNameInjector();
    }

    @Test
    public void validRestResponseInjectsName() {
        BasicOntology mockedResponse = createBasicOntology(1);
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        when(mockRestFetcher.<BasicOntology>convert(any())).thenReturn(stubConvertedFilter);
        Annotation annotation = new Annotation();
        assertThat(annotation.goName, is(nullValue()));

        nameInjector.inject(mockRestFetcher, annotation);

        assertThat(annotation.goName, is(mockedResponse.getResults().get(0).getName()));
    }

    @Test
    public void validRestResponseWithTwoResultsInjectsFirstName() {
        BasicOntology mockedResponse = createBasicOntology(2);
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        when(mockRestFetcher.<BasicOntology>convert(any())).thenReturn(stubConvertedFilter);
        Annotation annotation = new Annotation();
        assertThat(annotation.goName, is(nullValue()));
        assertThat(mockedResponse.getResults().get(0).getName(), is(not(mockedResponse.getResults().get(1).getName())));

        nameInjector.inject(mockRestFetcher, annotation);

        assertThat(annotation.goName, is(mockedResponse.getResults().get(0).getName()));
    }

    @Test
    public void restResponse404LeavesNullAnnotationGoName() {
        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());
        Annotation annotation = new Annotation();
        assertThat(annotation.goName, is(nullValue()));

        nameInjector.inject(mockRestFetcher, annotation);

        assertThat(annotation.goName, is(nullValue()));
    }

    @Test(expected = RetrievalException.class)
    public void restResponse5XXLeavesNullAnnotationGoName() {
        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());
        Annotation annotation = new Annotation();

        nameInjector.inject(mockRestFetcher, annotation);
    }

    private BasicOntology createBasicOntology(int resultCount) {
        BasicOntology ontology = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();
        for (int i = 0; i < resultCount; i++) {
            BasicOntology.Result result = new BasicOntology.Result();
            result.setId("ID:" + i);
            result.setName("Name:" + i);
            results.add(result);
        }
        ontology.setResults(results);
        return ontology;
    }
}