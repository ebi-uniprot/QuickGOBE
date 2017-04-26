package uk.ac.ebi.quickgo.rest.search.results.transformer;

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
 * a valid REST response will have its value injected into a model instance; a 404 response will leave the
 * instance untouched; other http status codes will lead to an exception.
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
        ConcreteModel model = new ConcreteModel();
        assertThat(model.id, is(nullValue()));

        injector.inject(mockRestFetcher, model);

        assertThat(model.id, is(idFromResponse));
    }

    @Test
    public void nonFatalRestResponseLeavesNullModelId() {
        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());
        ConcreteModel model = new ConcreteModel();
        assertThat(model.id, is(nullValue()));

        injector.inject(mockRestFetcher, model);

        assertThat(model.id, is(nullValue()));
    }

    @Test(expected = RetrievalException.class)
    public void fatalRestResponseCausesException() {
        ExecutionException executionException =
                new ExecutionException(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        doThrow(new RetrievalException(executionException)).when(mockRestFetcher).convert(any());
        ConcreteModel model = new ConcreteModel();

        injector.inject(mockRestFetcher, model);
    }

    private static class ConcreteValueInjector extends AbstractValueInjector<ConcreteResponse, ConcreteModel> {

        @Override public String getId() {
            // not required in test
            return null;
        }

        @Override
        public FilterRequest buildFilterRequest(ConcreteModel model) {
            // not required in test
            return null;
        }

        @Override
        public void injectValueFromResponse(
                ConvertedFilter<ConcreteResponse> convertedRequest, ConcreteModel model) {
            model.id = convertedRequest.getConvertedValue().idFromResponse;
        }

    }

    private static class ConcreteResponse implements ResponseType {
        ConcreteResponse(String idFromResponse) {
            this.idFromResponse = idFromResponse;
        }

        String idFromResponse;
    }

    private static class ConcreteModel {
        String id;
    }
}