package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverter.*;

/**
 * Created 20/06/16
 * @author Edd
 */
public class RESTFilterConverterTest {
    @Test
    public void fetchesRESTResourcesInfoByJsonPath() {
        FilterRequest filter = FilterRequest.newBuilder()
                .addProperty("id", "GO:0006915")
                .build();

        FilterConfig config = new FilterConfig();
        config.setExecution(FilterConfig.ExecutionType.REST_COMM);
        Map<String, String> configMap = new HashMap<>();
        configMap.put(HOST, "http://ves-hx-c2:8082");
        configMap.put(BODY_PATH, "$.results[*].ancestors[*]");
        configMap.put(RESOURCE_FORMAT, "/QuickGO/services/go/terms/{id}/complete");
        configMap.put(LOCAL_FIELD, "goId");
        config.setProperties(configMap);

        RESTFilterConverter converter = new RESTFilterConverter(config);

        QuickGOQuery query = converter.transform(filter);
        System.out.println(query);
    }

    @Test
    public void test2() {
        RestTemplate template = new RestTemplate();
        Map<String, String> m = new HashMap<>();
        m.put("id", "GO:0006915");
        String forObject = template.getForObject("http://ves-hx-c2:8082/QuickGO/services/go/terms/{id}/complete", String
                .class, m);
        System.out.println(forObject);
    }

    @Test
    public void buildsResourceTemplateWithHostNameOnlyAndResourceOnly() {
        FilterConfig config = new FilterConfig();
        config.setExecution(FilterConfig.ExecutionType.REST_COMM);
        Map<String, String> configMap = new HashMap<>();
        String host = "ves-hx-c2:8082";
        configMap.put(HOST, host);
        String resource = "/QuickGO/services/go/terms/{id}/complete";
        configMap.put(RESOURCE_FORMAT, resource);
        config.setProperties(configMap);

        String resourceTemplate = buildResourceTemplate(config);
        assertThat(resourceTemplate, is("http://" + host + resource));
    }

    @Test
    public void buildsResourceTemplateWithHostNameEndingInForwardSlashAndResourceOnly() {
        FilterConfig config = new FilterConfig();
        config.setExecution(FilterConfig.ExecutionType.REST_COMM);
        Map<String, String> configMap = new HashMap<>();
        String ip = "ves-hx-c2:8082";
        String host = ip + "/";
        configMap.put(HOST, host);
        String resourceNoLeadingSlash = "QuickGO/services/go/terms/{id}/complete";
        configMap.put(RESOURCE_FORMAT, resourceNoLeadingSlash);
        config.setProperties(configMap);

        String resourceTemplate = buildResourceTemplate(config);
        assertThat(resourceTemplate, is("http://" + ip + "/" + resourceNoLeadingSlash));
    }

    @Test
    public void buildsResourceTemplateWithHttpHostNameAndResourceOnly() {
        FilterConfig config = new FilterConfig();
        config.setExecution(FilterConfig.ExecutionType.REST_COMM);
        Map<String, String> configMap = new HashMap<>();
        String ip = "ves-hx-c2:8082";
        String host = "http://" + ip + "/";
        configMap.put(HOST, host);
        String resource = "QuickGO/services/go/terms/{id}/complete";
        configMap.put(RESOURCE_FORMAT, resource);
        config.setProperties(configMap);

        String resourceTemplate = buildResourceTemplate(config);
        assertThat(resourceTemplate, is("http://" + ip + "/" + resource));
    }

}