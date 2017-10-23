package uk.ac.ebi.quickgo.annotation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGAF;
import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGPAD;
import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToTSV;
import uk.ac.ebi.quickgo.annotation.download.http.*;
import uk.ac.ebi.quickgo.annotation.service.converter.StatisticsToWorkbook;
import uk.ac.ebi.quickgo.rest.controller.response.NoFacetNoHighlightNoAggregateQueryResult;
import uk.ac.ebi.quickgo.rest.controller.response.NoNextCursorPageInfo;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;
import static uk.ac.ebi.quickgo.annotation.service.converter.StatisticsWorkBookLayout.SECTION_TYPES;
import static uk.ac.ebi.quickgo.annotation.service.converter.StatisticsWorkBookLayout.SHEET_LAYOUT_MAP;

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
    public HttpMessageConverter gpadHttpMessageConverter() {
        return new HttpMessageConverter(gpadDispatchWriter(), GPAD_MEDIA_TYPE);
    }

    private OutputStreamWriter gpadDispatchWriter() {
        return new AnnotationDispatchWriter(new AnnotationToGPAD(), GPAD_MEDIA_TYPE);
    }

    @Bean
    public HttpMessageConverter gafHttpMessageConverter() {
        return new HttpMessageConverter(gafDispatchWriter(),GAF_MEDIA_TYPE);
    }

    private OutputStreamWriter gafDispatchWriter() {
        return new AnnotationDispatchWriter(new AnnotationToGAF(), GAF_MEDIA_TYPE);
    }

    @Bean
    public HttpMessageConverter tsvHttpMessageConverter(){
        return new HttpMessageConverter(tsvDispatchWriter(), TSV_MEDIA_TYPE);
    }

    private OutputStreamWriter tsvDispatchWriter() {
        return new AnnotationDispatchWriter(new AnnotationToTSV(), TSV_MEDIA_TYPE);
    }

    @Bean
    public HttpMessageConverter excelHttpMessageConverter(){
        return new HttpMessageConverter(statsDispatchWriter(), EXCEL_MEDIA_TYPE);
    }

    private OutputStreamWriter statsDispatchWriter() {
        return new StatsExcelDispatchWriter(new StatisticsToWorkbook(SECTION_TYPES, SHEET_LAYOUT_MAP));
    }
}
