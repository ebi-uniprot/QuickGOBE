package uk.ac.ebi.quickgo.cache.query.service;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;

/**
 * Tests for CacheRetrieval class
 * 
 * @author cbonill
 * 
 */
public class CacheRetrievalTest {

	// Mock context
	private Mockery context;
	private CacheRetrieval<GOTerm> cacheRetrieval;
	private CacheBuilder<GOTerm> cacheBuilder;

	@Before
	public void before() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		context = new Mockery();
		cacheRetrieval = new CacheRetrievalImpl();
		
		// Mock
		cacheBuilder = context.mock(CacheBuilder.class);
				
		// Set cacheBuilder value in cacheRetrieval
		Field fieldCurrencyServices = cacheRetrieval.getClass().getDeclaredField("cacheBuilder");
		fieldCurrencyServices.setAccessible(true);
		fieldCurrencyServices.set(cacheRetrieval, cacheBuilder);
	}

	/**
	 * Not cached entry 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveNotCachedEntry() throws Exception {

		String id = "GO:000011";
		final GOTerm createdTerm = new GOTerm(id,"name", "P","N");		

		context.checking(new Expectations() {
			{
				allowing(cacheBuilder).cachedValue(with(any(String.class)));
				will(returnValue(null));

				allowing(cacheBuilder).addEntry(with(any(String.class)),
						with(any(Class.class)));
				will(returnValue(createdTerm));
			}
		});

		GOTerm term = (GOTerm) cacheRetrieval.retrieveEntry(id, GOTerm.class);
		context.assertIsSatisfied();
		assertTrue(createdTerm.getId() == term.getId());
	}
	
	
	/**
	 * Cached entry
	 * @throws Exception
	 */
	@Test
	public void testRetrieveCachedEntry() throws Exception {

		String id = "GO:000011";
		final GOTerm createdTerm = new GOTerm(id, "name", "P", "N");

		context.checking(new Expectations() {
			{
				allowing(cacheBuilder).cachedValue(with(any(String.class)));
				will(returnValue(createdTerm));
			}
		});

		GOTerm term = (GOTerm) cacheRetrieval.retrieveEntry(id, GOTerm.class);
		context.assertIsSatisfied();
		assertTrue(createdTerm.getId() == term.getId());
	}
}