package uk.ac.ebi.quickgo.ontology.service.search;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.quickgo.rest.search.solr.SolrRetrievalConfigHelper.*;
import static uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfigHelper.extractFieldMappings;

@Configuration
@Import({OntologyRepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.ontology.service.search"})
@PropertySource("classpath:search.properties")
public class SearchServiceConfig {
    public static final String SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER = "/search";

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
            OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {

        SolrQueryResultConverter resultConverter = new SolrQueryResultConverter(
                new DocumentObjectBinder(),
                new GODocConverter(),
                new ECODocConverter(),
                ontologyRetrievalConfig.repo2DomainFieldMap()
        );

        return new SolrRequestRetrieval<>(
                ontologyTemplate.getSolrClient(),
                solrSelectQueryRequestConverter,
                resultConverter,
                ontologyRetrievalConfig);
    }

    @Bean public QueryRequestConverter<SolrQuery> ontologySolrQueryRequestConverter() {
        return SolrQueryConverter.create(SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER);
    }

    @Bean
    public OntologyCompositeRetrievalConfig ontologyRetrievalConfig(
            @Value("${search.return.fields:" + DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS + "}") String ontologySearchSolrReturnedFields,
            @Value("${search.field.repo2domain.map:}") String ontologySearchRepo2DomainFieldMap,
            @Value("${search.highlight.delims:" + DEFAULT_HIGHLIGHT_DELIMS + "}") String highlightDelims) {
        String[] highlightDelimsArr = convertHighlightDelims(highlightDelims, COMMA);

        return new OntologyCompositeRetrievalConfig() {

            @Override public Map<String, String> repo2DomainFieldMap() {
                return extractFieldMappings(ontologySearchRepo2DomainFieldMap, COMMA);
            }

            @Override public List<String> getSearchReturnedFields() {
                return Arrays.asList(ontologySearchSolrReturnedFields.split(COMMA));
            }

            @Override public String getHighlightStartDelim() {
                return highlightDelimsArr[HIGHLIGHT_START_DELIM_INDEX];
            }

            @Override public String getHighlightEndDelim() {
                return highlightDelimsArr[HIGHLIGHT_END_DELIM_INDEX];
            }
        };
    }

    public interface OntologyCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {}
}
