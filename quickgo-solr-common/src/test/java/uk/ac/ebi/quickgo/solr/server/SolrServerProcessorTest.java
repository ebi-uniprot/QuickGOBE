package uk.ac.ebi.quickgo.solr.server;

import java.util.Arrays;

import org.apache.lucene.index.Term;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests for SolRServerProcessor class
 * 
 * @author cbonill
 * 
 */
public class SolrServerProcessorTest {

	SolrServerProcessor solRServerProcessor = new SolrServerProcessorImpl();
	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	// Mock
	final HttpSolrServer httpSolrServer = context.mock(HttpSolrServer.class);	
	
	/**
	 * Null solR server URL. NullPointerException expected
	 * 
	 * @throws SolrServerException
	 */
	@Test(expected = NullPointerException.class)
	public void testNullSolrURL() throws SolrServerException {
		((SolrServerProcessorImpl) solRServerProcessor).setSolrURL(null);
		solRServerProcessor.findByQuery(new SolrQuery(), Term.class, -1);
		context.assertIsSatisfied();
	}

	/**
	 * Null query response
	 * 
	 * @throws SolrServerException
	 */
	@Test(expected = NullPointerException.class)
	public void testNullResults() throws SolrServerException {
		
		context.checking(new Expectations() {
			{
				allowing(httpSolrServer).query(with(any(SolrQuery.class)));
				will(returnValue(null));
			}
		});

		solRServerProcessor.findByQuery(new SolrQuery(), Term.class, -1);
		context.assertIsSatisfied();
	}

	/**
	 * Not null query response
	 * 
	 * @throws SolrServerException
	 */
	@Test
	public void testNotNullResults() throws SolrServerException {
		
		final QueryResponse queryResponse = context.mock(QueryResponse.class);
		((SolrServerProcessorImpl) solRServerProcessor).setSolrServer(httpSolrServer);

		context.checking(new Expectations() {
			{
				allowing(httpSolrServer).query(with(any(SolrQuery.class)));
				will(returnValue(queryResponse));

				allowing(queryResponse).getBeans(with(any(Class.class)));
				will(returnValue(Arrays.asList("Term")));
			}
		});

		solRServerProcessor.findByQuery(new SolrQuery(), Term.class, -1);
		context.assertIsSatisfied();
	}
}