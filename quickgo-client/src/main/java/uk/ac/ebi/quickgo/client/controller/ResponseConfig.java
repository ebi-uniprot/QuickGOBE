package uk.ac.ebi.quickgo.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.ac.ebi.quickgo.rest.controller.response.NoAggregateQueryResult;
import uk.ac.ebi.quickgo.rest.controller.response.NoCursorMarkPageInfo;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();
        mixinMap.put(QueryResult.class, NoAggregateQueryResult.class);
        mixinMap.put(PageInfo.class, NoCursorMarkPageInfo.class);
        mapper.setMixIns(Collections.unmodifiableMap(mixinMap));
        return mapper;
    }
}
