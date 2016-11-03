package uk.ac.ebi.quickgo.annotation;

import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.SwaggerConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * The RESTful service configuration for Annotations
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
@SpringBootApplication(exclude = {SolrRepositoriesAutoConfiguration.class})
@ComponentScan({"uk.ac.ebi.quickgo.annotation.controller",
        "uk.ac.ebi.quickgo.rest",
        "uk.ac.ebi.quickgo.annotation.service.statistics"})
@Import({SearchServiceConfig.class, SwaggerConfig.class})
public class AnnotationREST {

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        SpringApplication.run(AnnotationREST.class, args);
    }
}
