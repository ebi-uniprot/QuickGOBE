package uk.ac.ebi.quickgo.service.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequestConverter;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.data.solr.core.SolrTemplate;

import static java.util.Objects.requireNonNull;

/**
 *
 * Generic implementation that should be able to service the requirements of querying to most data sources.
 *
 * Created 18/01/16
 * @author Edd
 */
public class SolrRequestRetrieval<T> implements RequestRetrieval<T> {
    private SolrTemplate solrTemplate;
    private QueryResultConverter<T, QueryResponse> resultConverter;
    private QueryRequestConverter<SolrQuery> queryRequestConverter;

    public SolrRequestRetrieval(
            SolrTemplate solrTemplate,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            QueryResultConverter<T, QueryResponse> resultConverter) {
        this.solrTemplate = requireNonNull(solrTemplate);
        this.resultConverter = requireNonNull(resultConverter);
        this.queryRequestConverter = requireNonNull(queryRequestConverter);
    }

    @Override public QueryResult<T> findByQuery(QueryRequest request) {
        SolrQuery query = queryRequestConverter.convert(request);

        try {
            QueryResponse response = solrTemplate.getSolrServer().query(query);
            return resultConverter.convert(response, request);
        } catch (SolrServerException e) {
            throw new RetrievalException(e);
        }
    }
}
