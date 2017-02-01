package uk.ac.ebi.quickgo.annotation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGAF;
import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGPAD;
import uk.ac.ebi.quickgo.annotation.download.http.GAFHttpMessageConverter;
import uk.ac.ebi.quickgo.annotation.download.http.GPADHttpMessageConverter;
import uk.ac.ebi.quickgo.rest.controller.response.NoFacetNoHighlightNoAggregateQueryResult;
import uk.ac.ebi.quickgo.rest.controller.response.NoNextCursorPageInfo;
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
        mixinMap.put(QueryResult.class, NoFacetNoHighlightNoAggregateQueryResult.class);
        mixinMap.put(PageInfo.class, NoNextCursorPageInfo.class);
        mapper.setMixIns(Collections.unmodifiableMap(mixinMap));
        return mapper;
    }

    @Bean
    public GPADHttpMessageConverter gpadHttpMessageConverter() {
        return new GPADHttpMessageConverter(gpadAnnotationConverter());
    }

    @Bean
    public GAFHttpMessageConverter gafHttpMessageConverter() {
        return new GAFHttpMessageConverter(gafAnnotationConverter());
    }

    private AnnotationToGPAD gpadAnnotationConverter() {
        return new AnnotationToGPAD();
    }

    private AnnotationToGAF gafAnnotationConverter() {
        return new AnnotationToGAF();
    }
}
