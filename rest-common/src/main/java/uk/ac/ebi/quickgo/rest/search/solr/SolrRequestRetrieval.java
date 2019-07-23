package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.QueryResultConverter;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;

/**
 * Generic implementation that should be able to service the requirements of querying to most data sources.
 *
 * Created 18/01/16
 * @author Edd
 */
public class SolrRequestRetrieval<T> implements RequestRetrieval<T> {
    private SolrClient solrClient;
    private QueryResultConverter<T, QueryResponse> resultConverter;
    private QueryRequestConverter<SolrQuery> queryRequestConverter;

    public SolrRequestRetrieval(
            SolrClient solrClient,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            QueryResultConverter<T, QueryResponse> resultConverter,
            SolrRetrievalConfig serviceProperties) {
        this.solrClient = solrClient;
        this.resultConverter = resultConverter;
        this.queryRequestConverter = queryRequestConverter;

        checkArguments(solrClient, queryRequestConverter, resultConverter, serviceProperties);
    }

    private void checkArguments(SolrClient solrServer,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            QueryResultConverter<T, QueryResponse> resultConverter,
            SolrRetrievalConfig serviceProperties) {
        Preconditions.checkArgument(solrServer != null, "Solr server cannot be null");
        Preconditions.checkArgument(queryRequestConverter != null, "Query request converter cannot be null");
        Preconditions.checkArgument(resultConverter != null, "Response converter cannot be null");
        Preconditions.checkArgument(serviceProperties != null, "Request retrieval properties cannot be null");

        checkProperties(serviceProperties);
    }

    private void checkProperties(SolrRetrievalConfig serviceProperties) {
        Preconditions.checkArgument(serviceProperties.getHighlightStartDelim() != null, "The highlight start " +
                "delimiter cannot be null");
        Preconditions.checkArgument(serviceProperties.getHighlightEndDelim() != null, "The highlight end delimiter " +
                "cannot be null");
        Preconditions.checkArgument(serviceProperties.getSearchReturnedFields() != null, "The default return search " +
                "fields cannot be null");
    }

    @Override public QueryResult<T> findByQuery(QueryRequest request) {
        SolrQuery query = queryRequestConverter.convert(request);

        try {
            QueryResponse response = solrClient.query(request.getCollection(), query);
            return resultConverter.convert(response, request);
        } catch (SolrServerException | SolrException | IOException e) {
            throw new RetrievalException(e);
        }
    }
}
