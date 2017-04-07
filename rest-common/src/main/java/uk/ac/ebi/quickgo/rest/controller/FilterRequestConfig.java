package uk.ac.ebi.quickgo.rest.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Defines the beans used by the filter request framework.
 *
 * Created 24/06/16
 * @author Edd
 */
@Configuration
public class FilterRequestConfig {
    @Bean
    public RestOperations restOperations() {
        // todo: use connection pooling? http://stackoverflow.com/questions/31869193/using-spring-rest-template-either-creating-too-many-connections-or-slow
        // ... to prevent exceeding local port range when performing numerous requests
        return new RestTemplate();
    }
}
