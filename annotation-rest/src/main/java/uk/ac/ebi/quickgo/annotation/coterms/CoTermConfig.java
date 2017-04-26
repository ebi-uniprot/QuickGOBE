package uk.ac.ebi.quickgo.annotation.coterms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static uk.ac.ebi.quickgo.annotation.coterms.CoTermRepositorySimpleMap.createEmptyRepository;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
@Configuration
@EnableConfigurationProperties(CoTermProperties.class)
public class CoTermConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoTermConfig.class);
    private CoTermProperties coTermProperties;

    /**
     * If we have been unable to load the CoTermRepository, do not propagate the exception as this will stop all
     * configuration completing and the ontology service will not be available. Instead return a repository instance
     * that contains no data. It will throw an error every time it is used to look up CoTerms for an id.
     * @return CoTerm repository instance.
     */
    @Bean
    public CoTermRepository coTermRepository() {
        CoTermRepositorySimpleMap coTermRepository;
        LOGGER.debug("Contents of coterm properties" + coTermProperties);
        try {
            coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(coTermProperties.manual,
                                                                                         coTermProperties.all,
                                                                                         coTermProperties.headerLines);
        } catch (Exception e) {
            final String errorMessage = "Failed to load co-occurring terms from 'MANUAL' source " +
                    (coTermProperties
                             .getManual() == null ? "unknown" : coTermProperties
                            .getManual().getDescription()) +
                    " or from 'ALL' source " +
                    (coTermProperties.getAll() == null ? "unknown" : coTermProperties.getAll().getDescription());
            LOGGER.error(errorMessage);
            coTermRepository = createEmptyRepository();
        }
        return coTermRepository;
    }

    @Autowired
    public void setCoTermProperties(CoTermProperties coTermProperties) {
        LOGGER.debug("Setting contents of coterm properties" + coTermProperties);
        this.coTermProperties = coTermProperties;
    }
}
