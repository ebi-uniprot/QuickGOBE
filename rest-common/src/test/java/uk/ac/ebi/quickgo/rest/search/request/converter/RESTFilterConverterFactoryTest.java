package uk.ac.ebi.quickgo.rest.search.request.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestOperations;
import uk.ac.ebi.quickgo.rest.comm.ResponseType;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverter.*;
import static uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactoryTest.FakeResponseConverter.buildConvertedResponse;

/**
 * Created 05/09/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class RESTFilterConverterFactoryTest {
    @Mock
    private FilterConfigRetrieval filterConfigRetrievalMock;
    @Mock
    private RestOperations restOperationsMock;

    private RESTFilterConverterFactory converter;

    @BeforeEach
    void setUp() {
        this.converter = new RESTFilterConverterFactory(filterConfigRetrievalMock, restOperationsMock);
    }

    @Test
    void nullRestOperationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverterFactory(filterConfigRetrievalMock, null));
    }

    @Test
    void nullConfigRetrievalThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverterFactory(null, restOperationsMock));
    }

    @Test
    void requestThatIsNotREST_COMMCausesIllegalStateException() {
        FilterConfig filterConfig = createRestFilterConfig("somewhere", String.class, String.class);
        filterConfig.setExecution(FilterConfig.ExecutionType.JOIN);

        FilterRequest request = FilterRequest.newBuilder().addProperty("anything").build();

        when(filterConfigRetrievalMock.getBySignature(request.getSignature()))
          .thenReturn(Optional.of(filterConfig));
        assertThrows(IllegalStateException.class, () -> converter.convert(request));
    }

    @Test
    void restTransformationThroughConvertersIsInvoked() {
        String value = "valueX";
        String field = "fieldX";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();

        String response = "toReturn";
        restResponseReturnsString(response);

        FilterConfig filterConfig = createRestFilterConfig("resource", String.class, FakeResponseConverter.class);

        doReturn(Optional.of(filterConfig)).
        when(filterConfigRetrievalMock).getBySignature(request.getSignature());

        ConvertedFilter<String> convertedFilter = converter.convert(request);
        assertThat(convertedFilter.getConvertedValue(), is(buildConvertedResponse(response)));
    }

    @SuppressWarnings("unchecked")
    private void restResponseReturnsString(String value) {
        FakeResponse response = new FakeResponse();
        response.fakeResponseValue = value;

        doReturn(response).when(restOperationsMock)
          .getForObject(anyString(), isA(Class.class), any(Map.class));
    }

    private FilterConfig createRestFilterConfig(
            String resource,
            Class<?> responseClass,
            Class<?> responseConverterClass) {
        FilterConfig config = new FilterConfig();
        config.setExecution(FilterConfig.ExecutionType.REST_COMM);

        Map<String, String> configMap = new HashMap<>();
        configMap.put(HOST, "www.a.useful.site.co.uk/");
        configMap.put(RESOURCE_FORMAT, resource);
        configMap.put(RESPONSE_CONVERTER_CLASS, responseConverterClass.getName());
        configMap.put(RESPONSE_CLASS, responseClass.getName());
        configMap.put(TIMEOUT, "2000");
        config.setProperties(configMap);

        return config;
    }

    static class FakeResponse implements ResponseType {
        String fakeResponseValue;
    }

    static class FakeResponseConverter implements FilterConverter<FakeResponse, String> {
        private static final String CONVERTED = "CONVERTED ";

        @Override public ConvertedFilter<String> transform(FakeResponse response) {
            return new ConvertedFilter<>(buildConvertedResponse(response.fakeResponseValue));
        }

        static String buildConvertedResponse(String response) {return CONVERTED + response;}
    }

}