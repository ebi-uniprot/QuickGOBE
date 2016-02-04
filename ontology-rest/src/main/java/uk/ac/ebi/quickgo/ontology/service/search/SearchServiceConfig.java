package uk.ac.ebi.quickgo.ontology.service.search;

import uk.ac.ebi.quickgo.common.search.RequestRetrieval;
import uk.ac.ebi.quickgo.common.search.SearchService;
import uk.ac.ebi.quickgo.common.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.common.search.query.SolrQueryConverter;
import uk.ac.ebi.quickgo.common.search.solr.SolrRequestRetrieval;
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.core.SolrTemplate;

@Configuration
@Import({RepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.service.search"})
public class SearchServiceConfig {
    public static final String SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER = "/search";

    private static final String COMMA = ",";
    private static final String DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS = "id,name,ontologyType";

    @Bean
    public SearchService<OBOTerm> ontologySearchService(RequestRetrieval<OBOTerm> ontologySolrRequestRetrieval) {
        return new OntologySearchServiceImpl(ontologySolrRequestRetrieval);
    }

    @Bean
    public RequestRetrieval<OBOTerm> ontologySolrRequestRetrieval(
            SolrTemplate ontologyTemplate,
            QueryRequestConverter<SolrQuery> solrSelectQueryRequestConverter,
            @Value("${search.return.fields}") String solrReturnFieldsInText) {

        OntologySolrQueryResultConverter resultConverter = new OntologySolrQueryResultConverter(
                new DocumentObjectBinder(),
                new GODocConverter(),
                new ECODocConverter()
        );

        String[] solrReturnFields = parseSolrReturnFields(solrReturnFieldsInText);

        return new SolrRequestRetrieval<>(
                ontologyTemplate.getSolrServer(),
                solrSelectQueryRequestConverter,
                resultConverter,
                solrReturnFields);
    }

    private String[] parseSolrReturnFields(String ontologySearchSolrReturnedFields) {
        String[] solrReturnFields;

        if (ontologySearchSolrReturnedFields != null) {
            solrReturnFields = ontologySearchSolrReturnedFields.split(COMMA);
        } else {
            solrReturnFields = DEFAULT_ONTOLOGY_SEARCH_RETURN_FIELDS.split(COMMA);
        }

        return solrReturnFields;
    }

    @Bean
    public QueryRequestConverter<SolrQuery> ontologySolrQueryRequestConverter() {
        return new SolrQueryConverter(SOLR_ONTOLOGY_QUERY_REQUEST_HANDLER);
    }
}