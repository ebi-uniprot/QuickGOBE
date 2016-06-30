package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;
import uk.ac.ebi.quickgo.rest.search.request.FilterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ExternalRequestConfigRetrievalIT.TestApplication.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles(profiles = {"ExternalFilterExecutionConfigIT"})
public class ExternalRequestConfigRetrievalIT {
    @Configuration
    @ComponentScan
    @EnableConfigurationProperties
    public static class TestApplication {

        @Profile(value = "ExternalFilterExecutionConfigIT")
        @Bean
        public SearchableDocumentFields dummySearchableDocumentFields() {
            return new SearchableDocumentFields() {
                @Override public boolean isDocumentSearchable(String field) {
                    return false;
                }

                @Override public Stream<String> searchableDocumentFields() {
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
    public void yamlPropertiesLoadedCorrectlyIntoBean() {
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
        map.put("endpoint", "endpoint");

        return FilterUtil.createExecutionConfigWithProps("goIds,relations,usage", ExecutionType.REST_COMM, map);
    }
}