package uk.ac.ebi.quickgo.ontology.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.ac.ebi.quickgo.rest.controller.response.NoCursorMarkPageInfo;
import uk.ac.ebi.quickgo.rest.controller.response.NoFacetNoHighlightNoAggregateQueryResult;
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
        mapper.setMixIns(Collections.singletonMap(QueryResult.class, NoFacetNoHighlightNoAggregateQueryResult.class));
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();
        mixinMap.put(QueryResult.class, NoFacetNoHighlightNoAggregateQueryResult.class);
        mixinMap.put(PageInfo.class, NoCursorMarkPageInfo.class);
        mapper.setMixIns(Collections.unmodifiableMap(mixinMap));
        return mapper;
    }
}
