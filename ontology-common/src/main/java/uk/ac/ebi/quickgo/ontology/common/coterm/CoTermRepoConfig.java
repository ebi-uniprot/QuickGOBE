package uk.ac.ebi.quickgo.ontology.common.coterm;

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
public class CoTermRepoConfig {

    @Value("${coterm.file.manual}")
    private Resource manualResource;

    @Value("${coterm.file.all}")
    private Resource allResource;



    @Bean
    public CoTermRepository coTermRepository(){
        CoTermLoader coTermLoader = new CoTermLoader(manualResource, allResource);
        coTermLoader.load();
        return new CoTermRepositorySimpleMap(coTermLoader.coTermsAll, coTermLoader.coTermsManual);
    }


}
