package uk.ac.ebi.quickgo.ontology;

import uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidECOTermId;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;

/**
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 13:32
 * Created with IntelliJ IDEA.
 *
 * Configure the beans related to the operation of the restful service - id validation helpers and configuration
 * object for page sizes.
 */
@Configuration
public class OntologyRestConfig {

    @Bean
    public OntologyPagingConfig ontologyPagingConfig(
            @Value("${ontology.default_page_size:25}") int defaultPageSize) {
        return () -> defaultPageSize;
    }

    @Bean
    public OBOControllerValidationHelper goValidationHelper(@Value("${ontology.max_page_size:600}") int maxPageSize) {
        return new OBOControllerValidationHelperImpl(maxPageSize, isValidGOTermId());
    }

    @Bean
    public OBOControllerValidationHelper ecoValidationHelper(@Value("${ontology.max_page_size:600}") int maxPageSize) {
        return new OBOControllerValidationHelperImpl(maxPageSize, isValidECOTermId());
    }

    public interface OntologyPagingConfig {
        int defaultPageSize();
    }
}
