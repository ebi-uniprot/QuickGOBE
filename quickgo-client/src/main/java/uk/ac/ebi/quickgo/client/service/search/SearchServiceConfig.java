package uk.ac.ebi.quickgo.client.service.search;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.converter.ontology.ECODocConverter;
import uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter;
import uk.ac.ebi.quickgo.client.service.search.ontology.OntologySearchServiceImpl;
import uk.ac.ebi.quickgo.client.service.search.ontology.OntologySolrQueryResultConverter;
import uk.ac.ebi.quickgo.common.search.RequestRetrieval;
import uk.ac.ebi.quickgo.common.search.SearchService;
import uk.ac.ebi.quickgo.common.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.common.search.query.SolrQueryConverter;
import uk.ac.ebi.quickgo.common.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.common.search.solr.SolrRetrievalConfig;
import uk.ac.ebi.quickgo.common.service.ServiceRetrievalConfig;
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
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

/**
 * Spring configuration for the service layer, which depends on the repositories
 * made available by {@link RepoConfig}. Services to additionally make accessible
 * are defined in specified the {@link ComponentScan} packages.
 *
 * Created 19/11/15
 * @author Edd
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.client.service.search"})
@Import({RepoConfig.class})
public class SearchServiceConfig {
    public static final String SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER = "/search";

    private static final String COMMA = ",";
    private static final String DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS = "id,name,ontologyType";

    @Bean
    public SearchService<OntologyTerm> ontologySearchService(
            RequestRetrieval<OntologyTerm> ontologySolrRequestRetrieval) {
        return new OntologySearchServiceImpl(ontologySolrRequestRetrieval);
    }

    @Bean
    public RequestRetrieval<OntologyTerm> ontologySolrRequestRetrieval(
            SolrTemplate ontologyTemplate,
            QueryRequestConverter<SolrQuery> solrSelectQueryRequestConverter,
            OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {

        OntologySolrQueryResultConverter resultConverter = new OntologySolrQueryResultConverter(
                new DocumentObjectBinder(),
                new GODocConverter(),
                new ECODocConverter(),
                ontologyRetrievalConfig.repo2DomainFieldMap()
        );


        return new SolrRequestRetrieval<>(
                ontologyTemplate.getSolrServer(),
                solrSelectQueryRequestConverter,
                resultConverter,
                ontologyRetrievalConfig);
    }

    @Bean
    public QueryRequestConverter<SolrQuery> ontologySolrQueryRequestConverter() {
        return new SolrQueryConverter(SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER);
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

    public interface OntologyCompositeRetrievalConfig extends SolrRetrievalConfig, ServiceRetrievalConfig {
    }
}