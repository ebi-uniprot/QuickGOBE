package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created by rantunes on 19/08/16.
 */
@Configuration
class ResponseConfig {
    @Primary
    @Bean
    static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setMixIns(Collections.singletonMap(QueryResult.class, QueryResultMixin.class));

        return mapper;
    }
}
