package uk.ac.ebi.quickgo.ontology.service.search;

import uk.ac.ebi.quickgo.common.search.RequestRetrieval;
import uk.ac.ebi.quickgo.common.search.SearchService;
import uk.ac.ebi.quickgo.common.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.common.search.query.SolrQueryConverter;
import uk.ac.ebi.quickgo.common.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.common.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.common.service.ServiceRetrievalConfig;
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;

import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.core.SolrTemplate;

import static uk.ac.ebi.quickgo.common.search.solr.SolrRetrievalConfigHelper.DEFAULT_HIGHLIGHT_DELIMS;
import static uk.ac.ebi.quickgo.common.search.solr.SolrRetrievalConfigHelper.HIGHLIGHT_END_DELIM_INDEX;
import static uk.ac.ebi.quickgo.common.search.solr.SolrRetrievalConfigHelper.HIGHLIGHT_START_DELIM_INDEX;
import static uk.ac.ebi.quickgo.common.search.solr.SolrRetrievalConfigHelper.convertHighlightDelims;
import static uk.ac.ebi.quickgo.common.service.ServiceRetrievalConfigHelper.extractFieldMappings;

@Configuration
@Import({RepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.service.search"})
public class SearchServiceConfig {
    public static final String SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER = "/search";

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceConfig.class);
    private static final String COMMA = ",";
    private static final String DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS = "id,name,ontologyType";

    @Bean
    public SearchService<OBOTerm> ontologySearchService(RequestRetrieval<OBOTerm> ontologySolrRequestRetrieval) {
        return new SearchServiceImpl(ontologySolrRequestRetrieval);
    }

    @Bean
    public RequestRetrieval<OBOTerm> ontologySolrRequestRetrieval(
            SolrTemplate ontologyTemplate,
            QueryRequestConverter<SolrQuery> solrSelectQueryRequestConverter,
            OntologyCompositeRetrievalConfig ontologySolrConfig) {

        SolrQueryResultConverter resultConverter = new SolrQueryResultConverter(
                new DocumentObjectBinder(),
                new GODocConverter(),
                new ECODocConverter(),
                ontologySolrConfig.repo2DomainFieldMap()
        );

        return new SolrRequestRetrieval<>(
                ontologyTemplate.getSolrServer(),
                solrSelectQueryRequestConverter,
                resultConverter,
                ontologySolrConfig);
    }

    @Bean
    public QueryRequestConverter<SolrQuery> ontologySolrQueryRequestConverter() {
        return new SolrQueryConverter(SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER);
    }

    @Bean
    public OntologyCompositeRetrievalConfig ontologySolrConfig(
            @Value("${search.return.fields:" + DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS + "}") String ontologySearchSolrReturnedFields,
            @Value("${search.field.repo2domain.map:}") String ontologySearchRepo2DomainFieldMap,
            @Value("${search.highlight.delims:" + DEFAULT_HIGHLIGHT_DELIMS + "}") String highlightDelims) {
        String[] highlightDelimsArr = convertHighlightDelims(highlightDelims, COMMA);

        return new OntologyCompositeRetrievalConfig() {

            @Override public Map<String, String> repo2DomainFieldMap() {
                return extractFieldMappings(ontologySearchRepo2DomainFieldMap, COMMA);
            }

            @Override public String[] getSearchReturnedFields() {
                return ontologySearchSolrReturnedFields.split(COMMA);
            }

            @Override public String getHighlightStartDelim() {
                return highlightDelimsArr[HIGHLIGHT_START_DELIM_INDEX];
            }

            @Override public String getHighlightEndDelim() {
                return highlightDelimsArr[HIGHLIGHT_END_DELIM_INDEX];
            }
        };
    }

    private interface OntologyCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {
    }


}