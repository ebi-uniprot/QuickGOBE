package uk.ac.ebi.quickgo.repo.solr.config;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequestConverter;
import uk.ac.ebi.quickgo.repo.solr.query.model.SolrQueryConverter;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created 19/01/16
 * @author Edd
 */
@Configuration
public class QueryConfig {

    public static final String SOLR_QUERY_REQUEST_HANDLER = "/select";

    @Bean
    public QueryRequestConverter<SolrQuery> solrQueryRequestConverter() {
        return new SolrQueryConverter(SOLR_QUERY_REQUEST_HANDLER);
    }
}
