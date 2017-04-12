package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.comm.ResponseType;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

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
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * This test class demonstrates the desired behaviour of {@link AbstractValueInjector} instances, whereby:
 * a valid REST response will have its value injected into an annotation; a 404 response will leave the
 * annotation untouched; other http status codes will lead to an exception.
 *
 * Created 12/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractValueInjectorTest {

    @Mock
    private RESTFilterConverterFactory mockRestFetcher;

    private ConcreteValueInjector injector;

    @Before
    public void setUp() {
        injector = new ConcreteValueInjector();
    }

    @Test
    public void validResponseCausesSuccessfulValueInjection() {
        String idFromResponse = "response id";
        ConcreteResponse concreteResponse = new ConcreteResponse(idFromResponse);
        ConvertedFilter<ConcreteResponse> stubConvertedFilter = new ConvertedFilter<>(concreteResponse);
        when(mockRestFetcher.<ConcreteResponse>convert(any())).thenReturn(stubConvertedFilter);
        Annotation annotation = new Annotation();
        assertThat(annotation.id, is(nullValue()));

        injector.inject(mockRestFetcher, annotation);

        assertThat(annotation.id, is(idFromResponse));
    }

    @Test
    public void restResponse404LeavesNullAnnotationGoName() {
        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());
        Annotation annotation = new Annotation();
        assertThat(annotation.id, is(nullValue()));

        injector.inject(mockRestFetcher, annotation);

        assertThat(annotation.id, is(nullValue()));
    }

    @Test(expected = RetrievalException.class)
    public void restResponse5XXCausesException() {
        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());
        Annotation annotation = new Annotation();

        injector.inject(mockRestFetcher, annotation);
    }

    private static class ConcreteValueInjector extends AbstractValueInjector<ConcreteResponse> {

        @Override public String getId() {
            // not required in test
            return null;
        }

        @Override FilterRequest buildFilterRequest(Annotation annotation) {
            // not required in test
            return null;
        }

        @Override void injectValueFromResponse(
                ConvertedFilter<ConcreteResponse> convertedRequest, Annotation annotation) {
            annotation.id = convertedRequest.getConvertedValue().idFromResponse;
        }

    }

    private static class ConcreteResponse implements ResponseType {
        ConcreteResponse(String idFromResponse) {
            this.idFromResponse = idFromResponse;
        }

        String idFromResponse;
    }
}