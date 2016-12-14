package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;

/**
 * Configuration class related to loading and using co-occurring terms information.
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
@Configuration
public class CoTermRepoTestConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoTermRepoTestConfig.class);
    static final String FAILED_RETRIEVAL = "failedRetrieval";
    static final String SUCCESSFUL_RETRIEVAL = "successfulRetrieval";

    @Value("${coterm.source.manual}")
    private Resource manualResource;

    @Value("${coterm.source.all}")
    private Resource allResource;

    @Value("${coterm.source.header.lines:1}")
    private int headerLines;

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Profile(SUCCESSFUL_RETRIEVAL)
    public CoTermRepository coTermRepository() throws IOException {
        CoTermRepositorySimpleMap coTermRepository;
        coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(manualResource, allResource, headerLines);
        return coTermRepository;
    }

    @Bean
    @Profile(FAILED_RETRIEVAL)
    public CoTermRepository failedCoTermLoading() throws IOException {
        return CoTermRepositorySimpleMap.createEmptyRepository();
    }
}
