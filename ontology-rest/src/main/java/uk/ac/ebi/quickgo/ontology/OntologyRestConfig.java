package uk.ac.ebi.quickgo.ontology;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.cache.CacheStrategy;

import java.time.LocalTime;
import java.util.function.Function;

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
@EnableConfigurationProperties(OntologyRestProperties.class)
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

    @Bean
    Function<LocalTime, Long> remainingCacheTime(OntologyRestProperties restProperties) {
        CacheStrategy cacheStrategy = new CacheStrategy();
        return cacheStrategy.maxAgeCountDown(restProperties.getStartTime(), restProperties.getEndTime());
    }

    public interface OntologyPagingConfig {
        int defaultPageSize();
    }

}
