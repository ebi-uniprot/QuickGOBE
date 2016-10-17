package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Configuration class related to loading and using co-occurring terms information.
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
@Configuration
public class CoTermRepoConfig {

    @Value("${coterm.source.manual}")
    private Resource manualResource;

    @Value("${coterm.source.all}")
    private Resource allResource;

    @Bean
    public CoTermRepository coTermRepository() {
        CoTermRepositorySimpleMap coTermRepository;
        try{
            coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(manualResource, allResource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load co-occurring terms from manual source " +
                    (manualResource!=null?manualResource.getDescription():"unknown") + " or from all source " +
                    (allResource!=null?allResource.getDescription():"unknown"));
        }
        return coTermRepository;
    }
}
