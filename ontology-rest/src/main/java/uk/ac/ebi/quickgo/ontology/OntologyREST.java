package uk.ac.ebi.quickgo.ontology;



import uk.ac.ebi.quickgo.ontology.coterms.CoTermConfig;
import uk.ac.ebi.quickgo.ontology.service.ServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.CORSFilter2;
import uk.ac.ebi.quickgo.rest.controller.SwaggerConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Runnable class to start an embedded server to host the defined RESTful components.
 *
 * Created 16/11/15
 * @author Edd
 */
@SpringBootApplication(exclude = {SolrRepositoriesAutoConfiguration.class})
@ComponentScan({
        "uk.ac.ebi.quickgo.ontology.controller",
        "uk.ac.ebi.quickgo.rest"})
@Import({ServiceConfig.class, SwaggerConfig.class, CORSFilter2.class, CoTermConfig.class})
public class OntologyREST {
    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        SpringApplication.run(OntologyREST.class, args);
    }
}
