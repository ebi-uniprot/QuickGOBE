package uk.ac.ebi.quickgo.solr.query.service.ontology;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Tests for {@link TermRetrievalImpl} class
 * 
 * @author cbonill
 * 
 */
public class TermRetrievalTest {

	// Mock context
	private Mockery context;
	private TermRetrieval termRetrieval;
	private SolrServerProcessor solRServerProcessor;
	private EntityMapper<SolrTerm , GOTerm> termEntityMapper;
	
	@Before
	public void before() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		context = new Mockery();
		termRetrieval = new TermRetrievalImpl();

		// Mock
		solRServerProcessor = context.mock(SolrServerProcessor.class);
		termEntityMapper = context.mock(EntityMapper.class);
		
		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = termRetrieval.getClass().getDeclaredField("serverProcessor");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(termRetrieval, solRServerProcessor);
		
		fieldCurrencyServices = termRetrieval.getClass().getDeclaredField("termEntityMapper");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(termRetrieval, termEntityMapper);
	}

	/**
	 * Entry exists 
	 */
	@Test
	public void testFindByIdExists() throws SolrServerException {

		String id = "GO:000001";
		SolrTerm solrTerm = new SolrTerm();
		solrTerm.setId(id);
		final List<SolrTerm> solrTerms = Arrays.asList(solrTerm);
		final List<SolrDocumentType> termType = SolrTermDocumentType.getAsInterfaces ();
		
		final GOTerm term = new GOTerm(id, "name", "P", "N");
		
		context.checking(new Expectations() {
			{
				allowing(solRServerProcessor).findByQuery(with(any(SolrQuery.class)), with(any(Class.class)), with(any(Integer.class)));
				will(returnValue(solrTerms));
				
				allowing(termEntityMapper).toEntityObject(solrTerms, termType);
				will(returnValue(term));
			}
		});

		GOTerm foundTerm = (GOTerm) termRetrieval.findById(id);
		context.assertIsSatisfied();
		assertTrue(foundTerm.getId() == term.getId());
	}
}