package uk.ac.ebi.quickgo.annotation.controller;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGAF;
import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGPAD;
import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToTSV;
import uk.ac.ebi.quickgo.annotation.download.http.*;
import uk.ac.ebi.quickgo.annotation.service.converter.WorkbookFromStatisticsImpl;
import uk.ac.ebi.quickgo.rest.controller.response.NoFacetNoHighlightNoAggregateQueryResult;
import uk.ac.ebi.quickgo.rest.controller.response.NoNextCursorPageInfo;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;
import static uk.ac.ebi.quickgo.annotation.service.converter.StatisticsWorkBookLayout.SHEET_LAYOUT_SET;

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
          .addMixIn(QueryResult.class, NoFacetNoHighlightNoAggregateQueryResult.class)
          .addMixIn(PageInfo.class, NoNextCursorPageInfo.class)
          .build();
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
        return new StatsExcelDispatchWriter(new WorkbookFromStatisticsImpl(SHEET_LAYOUT_SET));
    }
}
