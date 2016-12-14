package uk.ac.ebi.quickgo.ontology.coterms;

import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermRepository;
import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermRepositorySimpleMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
@Configuration
public class CoTermConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoTermConfig.class);

    @Value("${coterm.default.limit:50}")
    private int defaultLimit;

    @Value("${coterm.source.manual}")
    private Resource manualResource;

    @Value("${coterm.source.all}")
    private Resource allResource;

    @Value("${coterm.source.headerLines:1}")
    private int headerLines;

    /**
     * If we have been unable to load the CoTermRepository, do not propagate the exception as this will stop all
     * configuration completing and the ontology service will not be available. Instead return a repository instance
     * that contains no data. It will throw an error everytime it is used to look up CoTerms for an id.
     * @return CoTerm repository instance.
     */
    @Bean
    public CoTermRepository coTermRepository() {
        CoTermRepositorySimpleMap coTermRepository;
        try {
            coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(manualResource, allResource,
                                                                                         headerLines);
        } catch (Exception e) {
            LOGGER.error("Failed to load co-occurring terms from 'MANUAL' source " +
                                 (manualResource != null ? manualResource.getDescription() : "unknown") +
                                 " or from 'ALL' source " +
                                 (allResource != null ? allResource.getDescription() : "unknown"));
            coTermRepository = CoTermRepositorySimpleMap.createEmptyRepository();
        }
        return coTermRepository;
    }

}
