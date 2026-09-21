package uk.ac.ebi.quickgo.geneproduct.controller;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.ac.ebi.quickgo.rest.controller.response.NoAggregateQueryResult;
import uk.ac.ebi.quickgo.rest.controller.response.NoNextCursorPageInfo;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * Configures how the response to the client should be handled.
 *
 * @author Ricardo Antunes
 */
@Configuration class ResponseConfig {
    @Primary
    @Bean
    static JsonMapper objectMapper() {
        return JsonMapper.builder()
          .addMixIn(QueryResult.class, NoAggregateQueryResult.class)
          .addMixIn(PageInfo.class, NoNextCursorPageInfo.class)
          .build();
    }
}
