package uk.ac.ebi.quickgo.cache.query.service.term;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrieval;

/**
 * Some integration tests to test the terms in-memory cache and SoLR
 * 
 * @author cbonill
 * 
 */
public class GOTermCacheRetrievalIntegrationTest {

	static ApplicationContext appContext;
	static CacheRetrieval<GOTerm> cacheRetrieval;
	static TermRetrieval termRetrieval;

	/**
	 * Miscellaneous data cache
	 */
	public static void main(String[] args) {
		appContext = new ClassPathXmlApplicationContext("common-beans.xml", "src/main/resources/query-beans.xml");
		cacheRetrieval = (CacheRetrieval<GOTerm>) appContext.getBean("goTermCacheRetrieval");
		termRetrieval = (TermRetrieval) appContext.getBean("goTermRetrieval");
		
		List<GOTerm> allTerms = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		try {
			allTerms = termRetrieval.findAll();
		} catch (SolrServerException e) {			
			e.printStackTrace();
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time (ms) building all the Terms Basic Information: " + estimatedTime);
		
		// Get basic information for a few entries
		getEntry("GO:0000001");
		getEntry("GO:0000002");
		getEntry("GO:0000003");
	}

	/**
	 * Get an entry from SolR and once it's cached, get it from the in-memory cache
	 * @param entry Entry to retrieve
	 */
	private static GOTerm getEntry(String entry) {		

		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			GOTerm term = cacheRetrieval.retrieveEntry(ClientUtils.escapeQueryChars(entry), GOTerm.class);
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Term ID: " + term.getId());
			System.out.println("Term NAME: " + term.getName());
			System.out.println("Term ONTOLOGY: " + term.getAspect().description);
			System.out.println("Time (ms) getting information from SolR: "
					+ estimatedTime);

			// Second time it's already cached
			startTime = System.currentTimeMillis();
			term = cacheRetrieval.retrieveEntry(ClientUtils.escapeQueryChars(entry), GOTerm.class);
			estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time (ms) getting information from in-memory cache: "
					+ estimatedTime);
			System.out.println("====================");
			return term;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
