package uk.ac.ebi.quickgo.common.search.solr;

import uk.ac.ebi.quickgo.common.search.QueryResultConverter;
import uk.ac.ebi.quickgo.common.search.RequestRetrieval;
import uk.ac.ebi.quickgo.common.search.RetrievalException;
import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.query.QueryRequestConverter;
import uk.ac.ebi.quickgo.common.search.results.QueryResult;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
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
    private final String highlightStartDelim;
    private final String highlightEndDelim;
    private SolrServer solrServer;
    private QueryResultConverter<T, QueryResponse> resultConverter;
    private QueryRequestConverter<SolrQuery> queryRequestConverter;
    private final String[] retrievedSolrFields;


    public SolrRequestRetrieval(
            SolrServer solrServer,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            QueryResultConverter<T, QueryResponse> resultConverter,
            SolrRetrievalConfig serviceProperties) {
        this.solrServer = solrServer;
        this.resultConverter = resultConverter;
        this.queryRequestConverter = queryRequestConverter;
        this.retrievedSolrFields = serviceProperties.getSearchReturnedFields();
        this.highlightStartDelim = serviceProperties.getHighlightStartDelim();
        this.highlightEndDelim = serviceProperties.getHighlightEndDelim();
    }

    private void checkArguments(SolrServer solrServer,
            QueryRequestConverter<SolrQuery> queryRequestConverter,
            QueryResultConverter<T, QueryResponse> resultConverter,
            SolrRetrievalConfig serviceProperties) {
        Preconditions.checkArgument(solrServer != null, "Solr server can not be null");
        Preconditions.checkArgument(queryRequestConverter != null, "Query request converter can not be null");
        Preconditions.checkArgument(resultConverter != null, "Response converter can not be null");
        Preconditions.checkArgument(serviceProperties != null, "Request retrieval properties can not be null");

        checkProperties(serviceProperties);
    }

    private void checkProperties(SolrRetrievalConfig serviceProperties) {
        Preconditions.checkArgument(serviceProperties.getHighlightStartDelim() != null, "The highlight start " +
                "delimiter can not be null");
        Preconditions.checkArgument(serviceProperties.getHighlightEndDelim() != null, "The highlight end delimiter " +
                "can not be null");
        Preconditions.checkArgument(serviceProperties.getSearchReturnedFields() != null, "The default return search " +
                "fields can not be null");
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

        query.setHighlightSimplePre(highlightStartDelim);
        query.setHighlightSimplePost(highlightEndDelim);
    }
}
