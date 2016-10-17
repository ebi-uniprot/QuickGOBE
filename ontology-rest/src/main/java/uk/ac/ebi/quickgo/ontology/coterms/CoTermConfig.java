package uk.ac.ebi.quickgo.ontology.coterms;

import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermRepository;
import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermRepositorySimpleMap;
import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import java.io.IOException;
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

    @Value("${coterm.default.limit:50}")
    private int defaultLimit;

    @Value("${coterm.source.manual}")
    private Resource manualResource;

    @Value("${coterm.source.all}")
    private Resource allResource;

    @Bean
    public CoTermLimit coTermLimit(){
        return new CoTermLimit(defaultLimit);
    }

    @Bean
    public CoTermRepository coTermRepository() {
        CoTermRepositorySimpleMap coTermRepository;
        try{
            coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(manualResource, allResource);
        } catch (IOException e) {
            throw new ServiceConfigException("Failed to load co-occurring terms from manual source " +
                    (manualResource!=null?manualResource.getDescription():"unknown") + " or from all source " +
                    (allResource!=null?allResource.getDescription():"unknown"));
        }
        return coTermRepository;
    }


}
