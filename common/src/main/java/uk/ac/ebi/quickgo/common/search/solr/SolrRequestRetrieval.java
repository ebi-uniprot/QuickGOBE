package uk.ac.ebi.quickgo.common.search.solr;

import uk.ac.ebi.quickgo.common.search.QueryResultConverter;
import uk.ac.ebi.quickgo.common.search.RequestRetrieval;
import uk.ac.ebi.quickgo.common.search.RetrievalException;
import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.common.search.results.QueryResult;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;

import static java.util.Objects.requireNonNull;

/**
 * Generic implementation that should be able to service the requirements of querying to most data sources.
 *
 * Created 18/01/16
 * @author Edd
 */
public class SolrRequestRetrieval<T> implements RequestRetrieval<T> {
    private SolrServer solrServer;
    private QueryResultConverter<T, QueryResponse> resultConverter;
    private QueryRequestConverter<SolrQuery> queryRequestConverter;
    private final String[] retrievedSolrFields;

    public SolrRequestRetrieval(
            SolrServer solrServer,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            QueryResultConverter<T, QueryResponse> resultConverter,
            String[] solrFieldsToRetrieve) {
        this.solrServer = requireNonNull(solrServer);
        this.resultConverter = requireNonNull(resultConverter);
        this.queryRequestConverter = requireNonNull(queryRequestConverter);
        this.retrievedSolrFields = requireNonNull(solrFieldsToRetrieve);
    }

    @Override public QueryResult<T> findByQuery(QueryRequest request) {
        SolrQuery query = queryRequestConverter.convert(request);

        configureQuery(query);

        try {
            QueryResponse response = solrServer.query(query);
            return resultConverter.convert(response, request);
        } catch (SolrServerException | SolrException | IllegalArgumentException e) {
            throw new RetrievalException(e);
        }
    }

    protected void configureQuery(SolrQuery query) {
        if (retrievedSolrFields.length > 0) {
            query.setFields(retrievedSolrFields);
        }
    }
}
