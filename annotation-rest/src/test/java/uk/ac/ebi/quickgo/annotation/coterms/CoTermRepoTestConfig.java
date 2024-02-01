package uk.ac.ebi.quickgo.annotation.coterms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

import static uk.ac.ebi.quickgo.annotation.coterms.CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap;
import static uk.ac.ebi.quickgo.annotation.coterms.CoTermRepositorySimpleMap.createEmptyRepository;

/**
 * Configuration class related to loading and using co-occurring terms information.
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
@Configuration
public class CoTermRepoTestConfig {
    static final String FAILED_RETRIEVAL = "failedRetrieval";
    static final String SUCCESSFUL_RETRIEVAL = "successfulRetrieval";

    private CoTermProperties coTermProperties = new CoTermProperties();

    @Bean
    @Profile(SUCCESSFUL_RETRIEVAL)
    public CoTermRepository coTermRepository() throws IOException {
        return createCoTermRepositorySimpleMap(coTermProperties.manual,
                                               coTermProperties.all,
                                               coTermProperties.headerLines);
    }

    @Bean
    @Profile(FAILED_RETRIEVAL)
    public CoTermRepository failedCoTermLoading() {
        return createEmptyRepository();
    }

    public void setCoTermProperties(CoTermProperties coTermProperties) {
        this.coTermProperties = coTermProperties;
    }
}
