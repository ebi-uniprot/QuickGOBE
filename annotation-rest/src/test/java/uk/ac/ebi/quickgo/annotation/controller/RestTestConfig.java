package uk.ac.ebi.quickgo.annotation.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;

/**
 * Provide instances of the RestTestSupport class for either GO Terms or ECO Terms.
 *
 * @author Tony Wardell
 * Date: 02/03/2017
 * Time: 13:57
 * Created with IntelliJ IDEA.
 */
@Configuration
public class RestTestConfig {

    @Bean
    RestTestSupport goRestTestSupport( RestOperations restOperations){
        return new RestTestSupport(restOperations, RestTestSupport.GO_DESCENDANTS_RESOURCE_FORMAT);
    }

    @Bean
    RestTestSupport ecoRestTestSupport( RestOperations restOperations){
        return new RestTestSupport(restOperations, RestTestSupport.ECO_DESCENDANTS_RESOURCE_FORMAT);
    }
}
