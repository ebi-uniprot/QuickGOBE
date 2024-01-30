package uk.ac.ebi.quickgo.rest.search.request.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.search.request.FilterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

@SpringBootTest(classes = ExternalRequestConfigRetrievalIT.TestApplication.class)
@ActiveProfiles(profiles = {"ExternalFilterExecutionConfigIT"})
class ExternalRequestConfigRetrievalIT {
    @Configuration
    @ComponentScan
    @EnableConfigurationProperties
    static class TestApplication {

        @Profile(value = "ExternalFilterExecutionConfigIT")
        @Bean
        public SearchableField dummySearchableDocumentFields() {
            return new SearchableField() {
                @Override public boolean isSearchable(String field) {
                    return false;
                }

                @Override public Stream<String> searchableFields() {
                    return Stream.empty();
                }
            };
        }

        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class);
        }
    }

    @Autowired
    private ExternalFilterConfigRetrieval externalFilterExecutionConfig;

    @Test
    void yamlPropertiesLoadedCorrectlyIntoBean() {
        List<FilterConfig> fieldConfigs = externalFilterExecutionConfig.getFilterConfigs();

        FilterConfig aspectField = createStubAspectSignature();
        FilterConfig usageField = createStubUsageSignature();

        assertThat(fieldConfigs, hasSize(2));
        assertThat(fieldConfigs, containsInAnyOrder(aspectField, usageField));
    }

    private FilterConfig createStubAspectSignature() {
        Map<String, String> map = new HashMap<>();
        map.put("fromTable", "ontology");
        map.put("fromAttribute", "id");
        map.put("toTable", "annotation");
        map.put("toAttribute", "id");

        return FilterUtil.createExecutionConfigWithProps("aspect", ExecutionType.JOIN, map);
    }

    private FilterConfig createStubUsageSignature() {
        Map<String, String> map = new HashMap<>();
        map.put("ip", "123.456.789");
        map.put("resourceFormat", "endpoint");
        map.put("localField", "goId");
        map.put("timeout", "4000");

        return FilterUtil.createExecutionConfigWithProps("goIds,goUsage,relations", ExecutionType.REST_COMM, map);
    }
}