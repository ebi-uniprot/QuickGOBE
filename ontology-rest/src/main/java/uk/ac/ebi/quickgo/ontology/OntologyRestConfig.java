package uk.ac.ebi.quickgo.ontology;

import uk.ac.ebi.quickgo.ontology.controller.ECOController;
import uk.ac.ebi.quickgo.ontology.controller.GOController;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 13:32
 * Created with IntelliJ IDEA.
 */
@Configuration
public class OntologyRestConfig {

    @Bean
    public OntologyPagingConfig ontologyPagingConfig(
            @Value("${ontology.default_page_size:25}") int defaultPageSize) {

        return () -> defaultPageSize;
    }

    public interface OntologyPagingConfig{
        int defaultPageSize();
    }

    @Bean
    public OBOControllerValidationHelper goValidationHelper(@Value("${ontology.max_page_size:600}") int maxPageSize){
        return new OBOControllerValidationHelperImpl(maxPageSize, GOController.idValidator());
    }

    @Bean
    public OBOControllerValidationHelper ecoValidationHelper(@Value("${ontology.max_page_size:600}") int maxPageSize){
        return new OBOControllerValidationHelperImpl(maxPageSize, ECOController.idValidator());
    }
}
