package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.comm.ResponseType;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.createQuery;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;
import static uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverter.*;

/**
 * Created 20/06/16
 * @author Edd
 */
@RunWith(Enclosed.class)
public class RESTFilterConverterTest {
    public static class ProcessingRESTResponses {

        private RESTRequesterImpl.Builder restRequestBuilderMock;
        private RESTRequesterImpl restRequesterMock;

        @Before
        public void setUp() {
            restRequestBuilderMock = mock(RESTRequesterImpl.Builder.class);
            restRequesterMock = mock(RESTRequesterImpl.class);
        }

        @Test
        public void fetchesSingleDatumFromRESTResources() {
            String resource = "/{id}/subresource";

            String field = "field";
            String restValue = "1";

            FakeResponse response = new FakeResponse();
            FakeResponse.Result result = new FakeResponse.Result();
            result.resultField = field;
            result.resultValue = restValue;
            response.results.add(result);

            setFutureRestResponse(FakeResponse.class, response);
            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            String id = "id";
            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty(id, "anything")
                    .build();

            RESTFilterConverter<QuickGOQuery> converter = createConverter(config);

            QuickGOQuery query = converter.transform(filter).getConvertedValue();

            assertThat(query, is(createQuery(field, restValue)));
        }

        @Test
        public void fetchesMultipleDataFromRESTResources() {
            String resource = "/{id}/subresource";

            String restValue1 = "1";
            String restValue2 = "2";
            String field = "field";

            FakeResponse response = new FakeResponse();
            FakeResponse.Result result1 = new FakeResponse.Result();
            result1.resultField = field;
            result1.resultValue = restValue1;
            FakeResponse.Result result2 = new FakeResponse.Result();
            result2.resultField = field;
            result2.resultValue = restValue2;

            response.results.add(result1);
            response.results.add(result2);

            setFutureRestResponse(FakeResponse.class, response);
            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            String id = "id";
            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty(id, "anything")
                    .build();

            RESTFilterConverter<QuickGOQuery> converter = createConverter(config);

            QuickGOQuery query = converter.transform(filter).getConvertedValue();

            assertThat(query, is(
                    or(
                            createQuery(field, restValue2),
                            createQuery(field, restValue1))));
        }

        @Test(expected = RetrievalException.class)
        public void failedExecutionOfRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            doThrow(ExecutionException.class).when(restRequesterMock).get(FakeResponse.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            converter.transform(filter);
        }

        @Test(expected = RetrievalException.class)
        public void timeoutOfRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            doThrow(TimeoutException.class).when(restRequesterMock).get(FakeResponse.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            converter.transform(filter);
        }

        @Test(expected = RetrievalException.class)
        public void interruptionOfRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            doThrow(InterruptedException.class).when(restRequesterMock).get(FakeResponse.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            converter.transform(filter);
        }

        @Test(expected = RetrievalException.class)
        public void invalidResponseClassCausesRetrievalException() {
            String resource = "/subresource";

            setFutureRestResponse(String.class, "this is wrong");
            FilterConfig config = createRestFilterConfig(resource, String.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            converter.transform(filter);
        }

        @Test(expected = RetrievalException.class)
        public void invalidResponseConverterClassCausesRetrievalException() {
            String resource = "/subresource";

            FakeResponse response = new FakeResponse();
            FakeResponse.Result result = new FakeResponse.Result();
            result.resultField = "field";
            result.resultValue = "value";
            response.results.add(result);

            setFutureRestResponse(FakeResponse.class, response);
            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, String.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            converter.transform(filter);
        }

        private <R> void setFutureRestResponse(Class<R> responseType, R response) {
            CompletableFuture<R> futureResponse = CompletableFuture.supplyAsync(() -> response);
            when(restRequesterMock.get(responseType)).thenReturn(futureResponse);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);
        }

        private FilterConfig createRestFilterConfig(
                String resource,
                Class<?> responseClass,
                Class<?> responseConverterClass) {
            FilterConfig config = new FilterConfig();
            config.setExecution(FilterConfig.ExecutionType.REST_COMM);

            Map<String, String> configMap = new HashMap<>();
            configMap.put(HOST, "www.golden.grahams.co.uk/");
            configMap.put(RESOURCE_FORMAT, resource);
            configMap.put(RESPONSE_CONVERTER_CLASS, responseConverterClass.getName());
            configMap.put(RESPONSE_CLASS, responseClass.getName());
            configMap.put(TIMEOUT, "2000");
            config.setProperties(configMap);

            return config;
        }

        private RESTFilterConverter<QuickGOQuery> createConverter(FilterConfig config) {
            return new RESTFilterConverter<QuickGOQuery>(config, mock(RestTemplate.class)) {
                @Override
                RESTRequesterImpl.Builder createRestRequesterBuilder() {
                    return restRequestBuilderMock;
                }
            };
        }
    }

    public static class BuildingResourceTemplates {

        private final String DEFAULT_RESOURCE = "/QuickGO";
        private String DEFAULT_HOST = "abc-def-ghi:8080";

        @Test
        public void buildsResourceTemplateWithHostNameOnlyAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, DEFAULT_HOST);
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(HTTPS_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        public void buildsResourceTemplateWithHostNameEndingInForwardSlashAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(HTTPS_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        public void buildsResourceTemplateWithHttpHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, "http://" + DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(HTTPS_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        public void buildsResourceTemplateWithHttpsHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, "https://" + DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(HTTPS_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test(expected = InvalidHostNameException.class)
        public void invalidCharacterInHostNameThrowsException() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, (DEFAULT_HOST + "?:8082") + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            buildResourceTemplate(config);
        }

        @Test(expected = InvalidHostNameException.class)
        public void invalidCharacterInHostsPortThrowsException() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, (DEFAULT_HOST + "&") + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            buildResourceTemplate(config);
        }

        private FilterConfig createRestFilterConfig() {
            FilterConfig config = new FilterConfig();
            config.setExecution(FilterConfig.ExecutionType.REST_COMM);
            return config;
        }

        private Map<String, String> createValidConfigMap() {
            Map<String, String> configMap = new HashMap<>();
            String ip = DEFAULT_HOST;
            String host = ip + "/";
            configMap.put(HOST, host);
            String resource = "/QuickGO/services/go/terms/{id}/complete";
            configMap.put(RESOURCE_FORMAT, resource);
            return configMap;
        }
    }

    public static class ValidatingInstantiationParameters {
        private FilterConfig filterConfig;

        private RestOperations restOperationsMock;

        @Before
        public void setUp() {
            this.filterConfig = new FilterConfig();
            this.filterConfig.setProperties(new HashMap<>());
            this.restOperationsMock = mock(RestOperations.class);
        }

        @Test
        public void successfullyCreateFilterConfigContainingMandatoryParameters() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(TIMEOUT, "1000");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        @Test(expected = IllegalArgumentException.class)
        public void nullFilterConfigCausesException() {
            new RESTFilterConverter(null, restOperationsMock);
        }

        @Test(expected = IllegalArgumentException.class)
        public void nullRestOperationsFilterConfigCausesException() {
            new RESTFilterConverter(filterConfig, null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void noHostCausesInstantiationException() {
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "1000");

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        @Test(expected = IllegalArgumentException.class)
        public void noResourceFormatCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "1000");

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        @Test(expected = IllegalArgumentException.class)
        public void noResponseClassCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(TIMEOUT, "1000");

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        @Test(expected = IllegalArgumentException.class)
        public void noResponseConverterClassCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "1000");

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        @Test(expected = IllegalArgumentException.class)
        public void noNumericalTimeoutCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "THIS IS NOT A NUMBER");

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        private void addConfigParam(String name, String value) {
            filterConfig.getProperties().put(name, value);
        }
    }

    static class FakeResponse implements ResponseType {
        List<Result> results = new ArrayList<>();

        static class Result {
            String resultField;
            String resultValue;
        }
    }

    static class FakeResponseConverter implements FilterConverter<FakeResponse, QuickGOQuery> {

        @Override public ConvertedFilter<QuickGOQuery> transform(FakeResponse response) {
            Set<QuickGOQuery> queries = response.results.stream()
                    .map(r -> QuickGOQuery.createQuery(r.resultField, r.resultValue))
                    .collect(Collectors.toSet());
            QuickGOQuery orQuery = or(queries.toArray(new QuickGOQuery[queries.size()]));

            return new ConvertedFilter<>(orQuery);
        }
    }
}