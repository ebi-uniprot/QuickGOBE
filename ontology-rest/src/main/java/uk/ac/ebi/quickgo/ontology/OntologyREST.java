package uk.ac.ebi.quickgo.ontology;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import uk.ac.ebi.quickgo.ontology.metadata.MetaDataConfig;
import uk.ac.ebi.quickgo.ontology.service.ServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.SwaggerConfig;

/**
 * Runnable class to start an embedded server to host the defined RESTful components.
 *
 * Created 16/11/15
 * @author Edd
 */
@SpringBootApplication
@ComponentScan({
        "uk.ac.ebi.quickgo.ontology.controller",
        "uk.ac.ebi.quickgo.rest"})
@Import({ServiceConfig.class, SwaggerConfig.class, OntologyRestConfig.class, MetaDataConfig.class})
public class OntologyREST {

    public static void main(String[] args) {
        SpringApplication.run(OntologyREST.class, args);
    }
}
