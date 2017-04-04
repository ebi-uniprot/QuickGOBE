package uk.ac.ebi.quickgo.ontology;

import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;

import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidECOTermId;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;

/**
 * Configure the beans related to the operation of the restful service - id validation helpers and configuration
 * object for page sizes.
 *
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 13:32
 * Created with IntelliJ IDEA.
 *
 */
@Configuration
public class OntologyRestConfig {

    private static final int MINUTES = 0;

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

    @Bean
    public LocalTime ontologyCacheStartTime(@Value("${ontology.cache.control.start.time:18}") int cacheStartHour){
        return LocalTime.of(cacheStartHour, MINUTES );
    }

    @Bean
    public LocalTime ontologyCacheEndTime(@Value("${ontology.cache.control.end.time:17}") int cacheEndHour){
        return LocalTime.of(cacheEndHour, MINUTES );
    }
}
