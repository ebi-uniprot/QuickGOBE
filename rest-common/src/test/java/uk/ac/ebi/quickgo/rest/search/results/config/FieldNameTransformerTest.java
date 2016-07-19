package uk.ac.ebi.quickgo.rest.search.results.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 19/07/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FieldNameTransformerTest.TestApplication.class,
        initializers = ConfigFileApplicationContextInitializer.class)
public class FieldNameTransformerTest {
    @Autowired
    private FieldNameTransformer transformer;

    @Test
    public void instantiate() {
        System.out.println(transformer);
        assertThat(transformer, is(notNullValue()));
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