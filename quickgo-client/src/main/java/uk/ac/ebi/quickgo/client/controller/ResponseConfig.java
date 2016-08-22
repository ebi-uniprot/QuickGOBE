package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.rest.controller.response.NoAggregateQueryResult;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configures how the response to the client should be handled.
 *
 * @author Ricardo Antunes
 */
@Configuration class ResponseConfig {
    @Primary
    @Bean
    static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setMixIns(Collections.singletonMap(QueryResult.class, NoAggregateQueryResult.class));

        return mapper;
    }
}
