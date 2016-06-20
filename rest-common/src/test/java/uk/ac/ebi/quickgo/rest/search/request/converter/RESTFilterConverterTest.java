package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

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
        configMap.put("ip", "http://ves-hx-c2:8082");
        configMap.put("responseBodyPath", "$.results[*].ancestors[*]");
        configMap.put("resourceFormat", "/QuickGO/services/go/terms/{id}/complete");
        configMap.put("localField", "goId");
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

}