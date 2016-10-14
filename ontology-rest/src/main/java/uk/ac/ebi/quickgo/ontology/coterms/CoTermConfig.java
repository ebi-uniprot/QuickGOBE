package uk.ac.ebi.quickgo.ontology.coterms;

import uk.ac.ebi.quickgo.ontology.common.coterms.CoTermRepoConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:50
 * Created with IntelliJ IDEA.
 */
@Configuration
@Import({CoTermRepoConfig.class})
public class CoTermConfig {

    @Value("${coterm.default.limit:50}")
    private int defaultLimit;

    @Bean
    public CoTermLimit coTermLimit(){
        return new CoTermLimit(defaultLimit);
    }


}
