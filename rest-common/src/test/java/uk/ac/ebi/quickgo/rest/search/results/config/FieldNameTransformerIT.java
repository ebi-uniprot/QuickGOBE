package uk.ac.ebi.quickgo.rest.search.results.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

/**
 * Created 19/07/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FieldNameTransformerIT.TestApplication.class)
public class FieldNameTransformerIT {
    @Autowired
    private FieldNameTransformer transformer;

    @Test
    public void checkAllFieldNameTransformationsLoaded() {
        assertThat(transformer.getTransformations().keySet(), hasSize(2));
    }

    @Test
    public void checkAllTransformationsAreLoaded() {
        assertThat(transformer.getTransformations(), is(createFieldNameTransformationMap()));
    }

    private Map<String, String> createFieldNameTransformationMap() {
        Map<String, String> transformations = new HashMap<>();
        transformations.put("field1", "transformedField1");
        transformations.put("field2", "transformedField2");
        return transformations;
    }

    @Configuration
    @ComponentScan
    @EnableConfigurationProperties
    public static class TestApplication {
        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class);
        }
    }
}