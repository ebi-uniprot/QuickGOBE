package uk.ac.ebi.quickgo.ontology;


import uk.ac.ebi.quickgo.ontology.common.config.RepoTestConfig;
import uk.ac.ebi.quickgo.ontology.config.ServiceConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Runnable class to start an embedded Jetty server to host the defined RESTful components.
 *
 * Created 16/11/15
 * @author Edd
 */
@SpringBootApplication
@ComponentScan(basePackages = {"uk.ac.ebi.quickgo.ontology.controller"})
@Import({ServiceConfig.class, RepoTestConfig.class})
public class QuickGOTestREST {
    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        SpringApplication.run(QuickGOTestREST.class, args);
    }
}
