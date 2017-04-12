package uk.ac.ebi.quickgo.ontology;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.cache.MaxAgeWhenStartBeforeEndTime;
import uk.ac.ebi.quickgo.rest.cache.MaxAgeWhenStartTimeAfterEndTime;
import uk.ac.ebi.quickgo.rest.headers.HttpHeader;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;

import org.springframework.http.HttpHeaders;

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

    public static final String MAX_AGE = "max-age";

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

    /**
     * Configure a HttpHeadersProvider instance to write required response HTTP headers.
     * @param restProperties holds properties to configure header sources.
     * @return HttpHeadersProvider instance
     */
    @Bean
    public HttpHeadersProvider httpHeadersProvider(OntologyRestProperties restProperties){

        List<HttpHeader> headerSources = new ArrayList<>();
        //We are assuming we are going to be doing daily indexing.
        Supplier<Duration> maxAge;
        if(restProperties.getStartTime().isAfter(restProperties.getEndTime())) {
            maxAge = new MaxAgeWhenStartTimeAfterEndTime(restProperties.getStartTime(), restProperties.getEndTime());
        }else {
            maxAge = new MaxAgeWhenStartBeforeEndTime(restProperties.getStartTime(), restProperties.getEndTime());
        }
        HttpHeader headerSource = new HttpHeader(HttpHeaders.CACHE_CONTROL, MAX_AGE,
                                                 ()-> Long.toString(maxAge.get().getSeconds()));
        headerSources.add(headerSource);


        return new HttpHeadersProvider(headerSources);
    }
}
