package uk.ac.ebi.quickgo.annotation;

import uk.ac.ebi.quickgo.annotation.search.SearchServiceConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
@SpringBootApplication(exclude = {SolrRepositoriesAutoConfiguration.class})
@ComponentScan({"uk.ac.ebi.quickgo.annotation.controller", "uk.ac.ebi.quickgo.rest"})
@Import({SearchServiceConfig.class})
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
