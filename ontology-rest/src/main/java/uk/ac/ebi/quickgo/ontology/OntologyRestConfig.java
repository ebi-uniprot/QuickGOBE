package uk.ac.ebi.quickgo.ontology;

import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.period.Period;
import uk.ac.ebi.quickgo.rest.period.RemainingTimeSupplier;
import uk.ac.ebi.quickgo.rest.period.DailyPeriodParser;
import uk.ac.ebi.quickgo.rest.headers.HttpHeader;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import static java.util.stream.Collectors.toList;
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

    Logger LOGGER = LoggerFactory.getLogger(OntologyRestConfig.class);
    public static final String MAX_AGE = "public, max-age";
    private static final String PERIOD_DELIMITER = ",";

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

    /**
     * Configure a HttpHeadersProvider instance to write required response HTTP headers.
     * @return HttpHeadersProvider instance
     */
    @Bean
    public HttpHeadersProvider httpHeadersProvider(RemainingTimeSupplier maxAgeProvider) {
        List<HttpHeader> headerSources = new ArrayList<>();
        HttpHeader headerSource = new HttpHeader(HttpHeaders.CACHE_CONTROL, MAX_AGE,
                                                 () -> Long.toString(maxAgeProvider.get().getSeconds()));
        headerSources.add(headerSource);
        return new HttpHeadersProvider(headerSources);
    }

    @Bean
    RemainingTimeSupplier maxAgeProvider(@Value("${ontology.caching.allowed.period}") String cachingAllowedPeriodValue,
            DailyPeriodParser dailyPeriodParser) {
        Collection<Period> cachingAllowedPeriods = null;
        if (Objects.nonNull(cachingAllowedPeriodValue)) {
            String[] periods = cachingAllowedPeriodValue.split(PERIOD_DELIMITER);
            try {
                cachingAllowedPeriods = Arrays.stream(periods)
                                              .map(s -> dailyPeriodParser.parse(s))
                                              .collect(toList());
            } catch (Exception e) {
                LOGGER.error("Failed to load caching allowed periods for ontology", e);
            }
        }
        RemainingTimeSupplier maxAgeProvider =
                new RemainingTimeSupplier(Objects.nonNull(cachingAllowedPeriods) ? cachingAllowedPeriods : new
                        ArrayList<>());
        return maxAgeProvider;
    }

    @Bean
    DailyPeriodParser periodParser() {
        return new DailyPeriodParser();
    }

    public interface OntologyPagingConfig {
        int defaultPageSize();
    }
}
