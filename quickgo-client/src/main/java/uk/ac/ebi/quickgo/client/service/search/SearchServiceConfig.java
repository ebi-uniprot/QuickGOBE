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
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.core.SolrTemplate;

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

    @Bean
    public SearchService<OntologyTerm> ontologySearchService(
            RequestRetrieval<OntologyTerm> ontologySolrRequestRetrieval) {
        return new OntologySearchServiceImpl(ontologySolrRequestRetrieval);
    }

    @Bean
    public RequestRetrieval<OntologyTerm> ontologySolrRequestRetrieval(
            SolrTemplate ontologyTemplate,
            QueryRequestConverter<SolrQuery> solrSelectQueryRequestConverter) {

        String[] retrieveAllFields = {};

        return new SolrRequestRetrieval<>(
                ontologyTemplate.getSolrServer(),
                solrSelectQueryRequestConverter,
                createSolrResultConverter(),
                retrieveAllFields);
    }

    private OntologySolrQueryResultConverter createSolrResultConverter() {
        return new OntologySolrQueryResultConverter(
                new DocumentObjectBinder(),
                new GODocConverter(),
                new ECODocConverter()
        );
    }

    @Bean
    public QueryRequestConverter<SolrQuery> ontologySolrQueryRequestConverter() {
        return new SolrQueryConverter(SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER);
    }
}