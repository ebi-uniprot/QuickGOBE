package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import static org.mockito.Mockito.mock;
import static org.springframework.data.solr.core.query.IfFunction.when;

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

    @Bean
    @Profile(SUCCESSFUL_RETRIEVAL)
    public CoTermRepository coTermRepository() throws IOException {
        CoTermRepositorySimpleMap coTermRepository;
        coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(manualResource, allResource);
        return coTermRepository;
    }

    @Bean
    @Profile(FAILED_RETRIEVAL)
    public CoTermRepository failedCoTermLoading() throws IOException {
        CoTermRepositorySimpleMap coTermRepository = null;
        Resource emptyResource = new FileSystemResource(File.createTempFile("CoTermRepoTestConfig", null, null));
        coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(emptyResource, emptyResource);
        return coTermRepository;
    }
}
