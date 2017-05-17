package uk.ac.ebi.quickgo.ontology;

import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.headers.HttpHeader;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;
import uk.ac.ebi.quickgo.rest.period.PeriodParserDayTime;
import uk.ac.ebi.quickgo.rest.period.AlarmClock;
import uk.ac.ebi.quickgo.rest.period.PeriodParser;
import uk.ac.ebi.quickgo.rest.period.RemainingTimeSupplier;

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

    private Logger LOGGER = LoggerFactory.getLogger(OntologyRestConfig.class);
    public static final String CACHE_CONTROL_HEADER = "public, max-age";
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
        HttpHeader headerSource = new HttpHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_HEADER,
                                                 () -> Long.toString(maxAgeProvider.getDuration().getSeconds()));
        return new HttpHeadersProvider(Collections.singletonList(headerSource));
    }

    @Bean
    RemainingTimeSupplier maxAgeProvider(@Value("${ontology.caching.allowed.period}") String cachingAllowedPeriodValue,
            PeriodParser parser) {
        LOGGER.info("Setting caching allowed period using " + cachingAllowedPeriodValue);
        Collection<AlarmClock> cachingAllowingAlarmClocks = null;
        if (Objects.nonNull(cachingAllowedPeriodValue)) {
            String[] periods = cachingAllowedPeriodValue.split(PERIOD_DELIMITER);
            try {
                cachingAllowingAlarmClocks = Arrays.stream(periods)
                                                  .map(parser::parse)
                                                  .filter(Optional::isPresent)
                                                  .map(Optional::get)
                                                  .collect(toList());
            } catch (Exception e) {
                LOGGER.error("Failed to load caching allowed periods for ontology using " + cachingAllowedPeriodValue,
                             e);
            }
        }
        return
                new RemainingTimeSupplier(Objects.nonNull(cachingAllowingAlarmClocks) ? cachingAllowingAlarmClocks :
                                                  Collections.emptyList());
    }

    @Bean
    PeriodParser periodParser() {
        return new PeriodParserDayTime();
    }

    public interface OntologyPagingConfig {
        int defaultPageSize();
    }
}
