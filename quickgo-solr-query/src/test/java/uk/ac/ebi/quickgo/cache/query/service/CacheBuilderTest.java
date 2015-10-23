package uk.ac.ebi.quickgo.cache.query.service;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrieval;

/**
 * Tests for the {@link CacheBuilderImpl} class
 * @author cbonill
 *
 */
public class CacheBuilderTest {

	// Mock context
	private Mockery context;
	private Retrieval<GOTerm> retrieval;
	private CacheBuilder<GOTerm> cacheBuilder;

	@Before
	public void before() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		context = new Mockery();
		cacheBuilder = new CacheBuilderImpl<GOTerm>();

		// Mock
		retrieval = context.mock(TermRetrieval.class);

		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = cacheBuilder.getClass().getDeclaredField("retrieval");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(cacheBuilder, retrieval);
	}
	
	/**
	 * Not cached entry and not in SolR
	 * @throws Exception
	 */
	@Test(expected=NotFoundException.class)
	public void testAddEntryNotCachedNotSolr() throws Exception {

		final String id = "1";

		context.checking(new Expectations() {
			{
				allowing(retrieval).findById(id);
				will(returnValue(null));				
			}
		});

		cacheBuilder.addEntry(id, GOTerm.class);
		context.assertIsSatisfied();
	}
	
	/**
	 * Add entry and get it from the cache
	 * @throws Exception 
	 */
	@Test
	public void testAddCachedEntry() throws Exception  {

		final String id = "GO:000011";
		final GOTerm term = new GOTerm(id,"name","P","N");
		
		// Not cached, add it
		
		context.checking(new Expectations() {
			{
				allowing(retrieval).findById(id);
				will(returnValue(term));		
			}
		});

		cacheBuilder.addEntry(id, GOTerm.class);
		context.assertIsSatisfied();

		// Once it's cached, get it

		GOTerm cachedTerm = cacheBuilder.addEntry(id, GOTerm.class);
		context.assertIsSatisfied();

		assertTrue(cachedTerm.getId() == id);
	}
	
	/**
	 * Not cached entry but it's in SolR
	 * @throws Exception
	 */
	@Test
	public void testAddEntryNotCachedYesSolr() throws Exception {

		final String id = "GO:000011";
		final GOTerm term = new GOTerm(id,"name","P","N");
		
		context.checking(new Expectations() {
			{
				allowing(retrieval).findById(id);
				will(returnValue(term));		
			}
		});

		cacheBuilder.addEntry(id, GOTerm.class);
		context.assertIsSatisfied();
		assertTrue(((CacheBuilderImpl<GOTerm>)cacheBuilder).getCachedValues().containsKey(id));
	}
}
