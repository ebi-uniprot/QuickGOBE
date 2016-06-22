package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
        public void fetchesSingleDatumFromRESTResourcesByJsonPath() {
            String resource = "/{id}/subresource";

            String restValue = "1";
            String field = "field";

            setFutureRestResponse("{message:\"" + restValue + "\"}");
            FilterConfig config = createRestFilterConfig(resource, "$.message", field);

            String id = "id";
            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty(id, "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            QuickGOQuery query = converter.transform(filter);

            assertThat(query, is(QuickGOQuery.createQuery(field, restValue)));
        }

        @Test
        public void fetchesMultipleDataFromRESTResourcesByJsonPath() {
            String resource = "/{id}/subresource";

            String restValue1 = "1";
            String restValue2 = "2";
            String field = "field";

            setFutureRestResponse("{results : [" +
                    "{message:\"" + restValue1 + "\"}," +
                    "{message:\"" + restValue2 + "\"}" +
                    "]}");
            FilterConfig config = createRestFilterConfig(resource, "$.results[*].message", field);

            String id = "id";
            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty(id, "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            QuickGOQuery query = converter.transform(filter);

            assertThat(query, is(QuickGOQuery.createQuery(field, restValue1 + "," + restValue2)));
        }

        @Test(expected = RetrievalException.class)
        public void failedRESTResponseCausesRetrievalException() {
            String resource = "/subresource";

            doThrow(ExecutionException.class).when(restRequesterMock).get(String.class);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);

            FilterConfig config = createRestFilterConfig(resource, "$.results[*].message", "field");

            FilterRequest filter = FilterRequest.newBuilder()
                    .addProperty("id", "anything")
                    .build();

            RESTFilterConverter converter = createConverter(config);

            converter.transform(filter);

        }

        private void setFutureRestResponse(String response) {
            CompletableFuture<String> futureResponse = CompletableFuture.supplyAsync(() -> response);
            when(restRequesterMock.get(String.class)).thenReturn(futureResponse);
            when(restRequestBuilderMock.build()).thenReturn(restRequesterMock);
        }

        private FilterConfig createRestFilterConfig(String resource, String bodyPath, String localField) {
            FilterConfig config = new FilterConfig();
            config.setExecution(FilterConfig.ExecutionType.REST_COMM);

            Map<String, String> configMap = new HashMap<>();
            configMap.put(HOST, "www.golden.grahams.co.uk/");
            configMap.put(RESOURCE_FORMAT, resource);
            configMap.put(BODY_PATH, bodyPath);
            configMap.put(LOCAL_FIELD, localField);
            config.setProperties(configMap);

            return config;
        }

        private RESTFilterConverter createConverter(FilterConfig config) {
            return new RESTFilterConverter(config, mock(RestTemplate.class)) {
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
            assertThat(resourceTemplate, is("http://" + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        public void buildsResourceTemplateWithHostNameEndingInForwardSlashAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is("http://" + DEFAULT_HOST + DEFAULT_RESOURCE));
        }

        @Test
        public void buildsResourceTemplateWithHttpHostNameAndResourceOnly() {
            FilterConfig config = createRestFilterConfig();
            Map<String, String> configMap = createValidConfigMap();
            configMap.put(HOST, "http://" + DEFAULT_HOST + "/");
            configMap.put(RESOURCE_FORMAT, DEFAULT_RESOURCE);
            config.setProperties(configMap);

            String resourceTemplate = buildResourceTemplate(config);
            assertThat(resourceTemplate, is("http://" + DEFAULT_HOST + DEFAULT_RESOURCE));
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

}