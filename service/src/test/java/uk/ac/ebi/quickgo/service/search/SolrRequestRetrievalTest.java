package uk.ac.ebi.quickgo.service.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequestConverter;
import uk.ac.ebi.quickgo.repo.solr.query.results.Facet;
import uk.ac.ebi.quickgo.repo.solr.query.results.PageInfo;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;

import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link SolrQuery} that gets dispatched to the {@link SolrServer}
 * has been configured correctly. Concrete class primarily delegates behaviour to
 * its own instance variables, which are tested in isolation.
 *
 * Created 21/01/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrRequestRetrievalTest {

    private static final String[] VALID_RETURN_FIELDS = new String[]{"field1", "field2"};
    private static final String[] ZERO_RETURN_FIELDS = new String[]{};
    private SolrServer solrServerMock;
    private QueryRequestConverter<SolrQuery> queryRequestConverterMock;
    private QueryResultConverter<SolrQuery, QueryResponse> resultConverterMock;
    private SolrQuery solrQueryMock;
    private QueryResult<SolrQuery> queryResultMock;

    @Before
    public void setUp() throws SolrServerException {
        solrServerMock = mock(SolrServer.class);
        queryRequestConverterMock = new FakeQueryRequestConverter();
        resultConverterMock = new FakeQueryResultConverter();
        solrQueryMock = mock(SolrQuery.class);
        queryResultMock = mock(FakeQueryResult.class);

    }

    private SolrRequestRetrieval<SolrQuery> createSolrRequestRetrieval(
            String[] returnFields) {
        return new SolrRequestRetrieval<>(
                solrServerMock,
                queryRequestConverterMock,
                resultConverterMock,
                returnFields);
    }

    @Test
    public void twoQueryReturnFieldsAreSetCorrectly() {
        SolrRequestRetrieval<SolrQuery> solrRequestRetrieval = createSolrRequestRetrieval(VALID_RETURN_FIELDS);

        SolrQuery query = mock(SolrQuery.class);
        solrRequestRetrieval.configureQuery(query);
        verify(query, times(1)).setFields(VALID_RETURN_FIELDS);
    }

    @Test
    public void zeroQueryReturnFieldsAreSet() {
        SolrRequestRetrieval<SolrQuery> solrRequestRetrieval = createSolrRequestRetrieval(ZERO_RETURN_FIELDS);

        SolrQuery query = mock(SolrQuery.class);
        solrRequestRetrieval.configureQuery(query);
        verify(query, times(0)).setFields(ZERO_RETURN_FIELDS);
    }

    private class FakeQueryRequestConverter implements QueryRequestConverter<SolrQuery> {
        @Override public SolrQuery convert(QueryRequest request) {
            return solrQueryMock;
        }
    }

    private class FakeQueryResultConverter implements QueryResultConverter<SolrQuery, QueryResponse> {
        @Override public QueryResult<SolrQuery> convert(QueryResponse toConvert, QueryRequest request) {
            return queryResultMock;
        }
    }

    private class FakeQueryResult extends QueryResult<SolrQuery> {
        public FakeQueryResult(long numberOfHits, List<SolrQuery> results,
                PageInfo pageInfo,
                Facet facet) {
            super(numberOfHits, results, pageInfo, facet);
        }
    }

}