package uk.ac.ebi.quickgo.rest.search.request.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.comm.ResponseType;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.createQuery;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;
import static uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverter.*;

/**
 * Created 20/06/16
 * @author Edd
 */
class RESTFilterConverterTest {
    @Nested
    class ProcessingRESTResponses {

        private RESTRequesterImpl.Builder restRequestBuilderMock;
        private RESTRequesterImpl restRequesterMock;

        @BeforeEach
        void setUp() {
            restRequestBuilderMock = mock(RESTRequesterImpl.Builder.class);
            restRequesterMock = mock(RESTRequesterImpl.class, RETURNS_DEEP_STUBS);
        }

        @Test
        void fetchesSingleDataFromRESTResources() {
            String resource = "/{id}/subresource";

            String field = "field";
            String restValue = "1";

            FakeResponse response = fakeResponse(field, restValue);

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
        void fetchesSingleDataFromBackupRESTResourcesWhenPrimaryFails()
          throws InterruptedException, ExecutionException, TimeoutException {
            String field = "field";
            String restValue = "1";

            FakeResponse response = fakeResponse(field, restValue);

            setRestResponsePrimaryFail(FakeResponse.class, response);
            FilterConfig config = createRestFilterConfig("/{id}/subresource", FakeResponse.class, FakeResponseConverter.class);
            FilterRequest filter = FilterRequest.newBuilder().addProperty("id", "anything").build();
            RESTFilterConverter<QuickGOQuery> converter = createConverter(config);

            QuickGOQuery query = converter.transform(filter).getConvertedValue();

            assertThat(query, is(createQuery(field, restValue)));
        }

        @Test
        void fetchDataFromRESTResourcesCauseExceptionWhenPrimaryAndBackupFails()
          throws InterruptedException, ExecutionException, TimeoutException {
            FakeResponse response = fakeResponse("field", "1");

            setRestResponseBothFail(FakeResponse.class, response);
            FilterConfig config = createRestFilterConfig("/{id}/subresource", FakeResponse.class, FakeResponseConverter.class);
            FilterRequest filter = FilterRequest.newBuilder().addProperty("id", "anything").build();
            RESTFilterConverter<QuickGOQuery> converter = createConverter(config);

            assertThrows(RetrievalException.class, ()-> converter.transform(filter));
        }

        @Test
        void fetchDataFromRESTResourcesCauseExceptionWhenPrimaryFailsAndBackupIsNotConfigured()
          throws InterruptedException, ExecutionException, TimeoutException {
            when(restRequesterMock.get(FakeResponse.class).get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenThrow(ExecutionException.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);
            FilterConfig config = createRestFilterConfig("/{id}/subresource", FakeResponse.class, FakeResponseConverter.class);
            FilterRequest filter = FilterRequest.newBuilder().addProperty("id", "anything").build();
            RESTFilterConverter<QuickGOQuery> converter = createConverter(config);

            assertThrows(RetrievalException.class, ()-> converter.transform(filter));
        }

        @Test
        void fetchesMultipleDataFromRESTResources() {
            String resource = "/{id}/subresource";

            String restValue1 = "1";
            String restValue2 = "2";
            String field = "field";

            FakeResponse response = fakeResponse(field, restValue1);
            FakeResponse.Result result2 = new FakeResponse.Result();
            result2.resultField = field;
            result2.resultValue = restValue2;

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

        @Test
        void failedExecutionOfRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            doThrow(RuntimeException.class).when(restRequesterMock).get(FakeResponse.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            assertThrows(RetrievalException.class, () -> converter.transform(filter));
        }

        @Test
        void timeoutOfRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            doThrow(RuntimeException.class).when(restRequesterMock).get(FakeResponse.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            assertThrows(RetrievalException.class, () -> converter.transform(filter));
        }

        @Test
        void interruptionOfRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            when(restRequesterMock.get(any())).thenThrow(RuntimeException.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            assertThrows(RetrievalException.class, () -> converter.transform(filter));
        }

        @Test
        void invalidResponseClassCausesRetrievalException() {
            String resource = "/subresource";

            setFutureRestResponse(String.class, "this is wrong");
            FilterConfig config = createRestFilterConfig(resource, String.class, FakeResponseConverter.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            assertThrows(RetrievalException.class, () -> converter.transform(filter));
        }

        @Test
        void invalidResponseConverterClassCausesRetrievalException() {
            String resource = "/subresource";

            FakeResponse response = fakeResponse("field", "value");

            setFutureRestResponse(FakeResponse.class, response);
            FilterConfig config = createRestFilterConfig(resource, FakeResponse.class, String.class);

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            assertThrows(RetrievalException.class, () -> converter.transform(filter));
        }

        private <R> void setFutureRestResponse(Class<R> responseType, R response) {
            CompletableFuture<R> futureResponse = CompletableFuture.supplyAsync(() -> response);
            when(restRequesterMock.get(responseType)).thenReturn(futureResponse);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);
        }

        private <R> void setRestResponsePrimaryFail(Class<R> responseType, R response)
          throws InterruptedException, ExecutionException, TimeoutException {
            when(restRequesterMock.hasBackup()).thenReturn(true);
            when(restRequesterMock.get(responseType).get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenThrow(ExecutionException.class);
            when(restRequesterMock.getBackup(responseType).get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(response);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);
        }

        private <R> void setRestResponseBothFail(Class<R> responseType, R response)
          throws InterruptedException, ExecutionException, TimeoutException {
            when(restRequesterMock.hasBackup()).thenReturn(true);
            when(restRequesterMock.get(responseType).get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenThrow(ExecutionException.class);
            when(restRequesterMock.getBackup(responseType).get(anyLong(), eq(TimeUnit.MILLISECONDS))).thenThrow(ExecutionException.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);
        }

        private FakeResponse fakeResponse(String field, String restValue){
            FakeResponse response = new FakeResponse();
            FakeResponse.Result result = new FakeResponse.Result();
            result.resultField = field;
            result.resultValue = restValue;
            response.results.add(result);
            return response;
        }

        private FilterConfig createRestFilterConfig(
                String resource,
                Class<?> responseClass,
                Class<?> responseConverterClass) {
            FilterConfig config = new FilterConfig();
            config.setExecution(FilterConfig.ExecutionType.REST_COMM);

            Map<String, String> configMap = new HashMap<>();
            configMap.put(HOST, "www.golden.grahams.co.uk/");
            configMap.put(BACKUP_HOST, "www.backup.host.co.uk/");
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

    @Nested
    class BuildingResourceTemplates {

        private static final String HTTPS = "https://";
        private final String DEFAULT_RESOURCE = "/QuickGO";
        private final String DEFAULT_HOST = "abc-def-ghi:8080";

        @Test
        void buildsResourceTemplateWithHostNameOnlyAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, DEFAULT_HOST);
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(DEFAULT_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void buildsResourceTemplateWithHostNameEndingInForwardSlashAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(DEFAULT_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void buildsResourceTemplateWithHttpHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, "http://" + DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(DEFAULT_PROTOCOL + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void buildsResourceTemplateWithHttpsHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, HTTPS + DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is(HTTPS + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void invalidCharacterInHostNameThrowsException() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, (DEFAULT_HOST + "?:8082") + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            assertThrows(InvalidHostNameException.class, () -> buildResourceTemplate(config));
        }

        @Test
        void invalidCharacterInHostsPortThrowsException() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, (DEFAULT_HOST + "&") + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            assertThrows(InvalidHostNameException.class, () -> buildResourceTemplate(config));
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

    @Nested
    class BuildingBackupResourceTemplates {

        private static final String HTTPS = "https://";
        private final String DEFAULT_RESOURCE = "/QuickGO";
        private final String DEFAULT_BACKUP_HOST = "back-up-vm:9080";

        @ParameterizedTest
        @NullAndEmptySource
        void buildBackupResourceTemplateWillBeEmpty_ifBackupHostProperty(String backupNullEmptyHost) {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, backupNullEmptyHost);
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildBackupResourceTemplate(config);
            assertThat(resourceTemplate, is(""));
        }

        @Test
        void buildsResourceTemplateWithBackupHostNameOnlyAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, DEFAULT_BACKUP_HOST);
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildBackupResourceTemplate(config);
            assertThat(resourceTemplate, is(DEFAULT_PROTOCOL + DEFAULT_BACKUP_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void buildsResourceTemplateWithHostNameEndingInForwardSlashAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, DEFAULT_BACKUP_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildBackupResourceTemplate(config);
            assertThat(resourceTemplate, is(DEFAULT_PROTOCOL + DEFAULT_BACKUP_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void buildsResourceTemplateWithHttpHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, "http://" + DEFAULT_BACKUP_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildBackupResourceTemplate(config);
            assertThat(resourceTemplate, is(DEFAULT_PROTOCOL + DEFAULT_BACKUP_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void buildsResourceTemplateWithHttpsHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, HTTPS + DEFAULT_BACKUP_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildBackupResourceTemplate(config);
            assertThat(resourceTemplate, is(HTTPS + DEFAULT_BACKUP_HOST + DEFAULT_RESOURCE));
        }

        @Test
        void invalidCharacterInHostNameThrowsException() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, (DEFAULT_BACKUP_HOST + "?:8082") + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            assertThrows(InvalidHostNameException.class, () -> buildBackupResourceTemplate(config));
        }

        @Test
        void invalidCharacterInHostsPortThrowsException() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(BACKUP_HOST, (DEFAULT_BACKUP_HOST + "&") + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            assertThrows(InvalidHostNameException.class, () -> buildBackupResourceTemplate(config));
        }

        private FilterConfig createRestFilterConfig() {
            FilterConfig config = new FilterConfig();
            config.setExecution(FilterConfig.ExecutionType.REST_COMM);
            return config;
        }

        private Map<String, String> createValidConfigMap() {
            Map<String, String> configMap = new HashMap<>();
            String host = DEFAULT_BACKUP_HOST + "/";
            configMap.put(BACKUP_HOST, host);
            String resource = "/QuickGO/services/go/terms/{id}/complete";
            configMap.put(RESOURCE_FORMAT, resource);
            return configMap;
        }
    }

    @Nested
    class ValidatingInstantiationParameters {
        private FilterConfig filterConfig;

        private RestOperations restOperationsMock;

        @BeforeEach
        void setUp() {
            this.filterConfig = new FilterConfig();
            this.filterConfig.setProperties(new HashMap<>());
            this.restOperationsMock = mock(RestOperations.class);
        }

        @Test
        void successfullyCreateFilterConfigContainingMandatoryParameters() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(TIMEOUT, "1000");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());

            new RESTFilterConverter(filterConfig, restOperationsMock);
        }

        @Test
        void nullFilterConfigCausesException() {
            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(null, restOperationsMock));
        }

        @Test
        void nullRestOperationsFilterConfigCausesException() {
            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(filterConfig, null));
        }

        @Test
        void noHostCausesInstantiationException() {
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "1000");

            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(filterConfig, restOperationsMock));
        }

        @Test
        void noResourceFormatCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "1000");

            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(filterConfig, restOperationsMock));
        }

        @Test
        void noResponseClassCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CONVERTER_CLASS, FakeResponseConverter.class.getName());
            addConfigParam(TIMEOUT, "1000");

            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(filterConfig, restOperationsMock));
        }

        @Test
        void noResponseConverterClassCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "1000");

            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(filterConfig, restOperationsMock));
        }

        @Test
        void noNumericalTimeoutCausesInstantiationException() {
            addConfigParam(HOST, "host");
            addConfigParam(RESOURCE_FORMAT, "resource format");
            addConfigParam(RESPONSE_CLASS, FakeResponse.class.getName());
            addConfigParam(TIMEOUT, "THIS IS NOT A NUMBER");

            assertThrows(IllegalArgumentException.class, () -> new RESTFilterConverter(filterConfig, restOperationsMock));
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