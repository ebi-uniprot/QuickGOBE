package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

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

        RequestConfig config = new RequestConfig();
        config.setExecution(RequestConfig.ExecutionType.REST_COMM);
        Map<String, String> map = new HashMap<>();
        map.put("ip", "http://ves-hx-c2:8082");
        //        map.put("responseBodyPath", "$.results.synonyms.synonymName");
        map.put("responseBodyPath", "$.results[*].synonyms[*].synonymName");
        map.put("endpoint", "/QuickGO/services/go/terms");
        config.setProperties(map);
        RESTFilterConverter converter = new RESTFilterConverter(config);

        converter.transform(filter);
    }

}