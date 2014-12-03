package uk.ac.ebi.quickgo.cache.query.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrieval;

/**
 * Some integration tests to test in-memory cache and SoLR
 * 
 * @author cbonill
 * 
 */
public class CacheRetrievalIntegrationTest {

	static ApplicationContext appContext;
	static CacheRetrieval<GOTerm> cacheRetrieval;
	static TermRetrieval termRetrieval;
	
	static CacheRetrieval<Miscellaneous> miscellaneousCacheRetrieval;
	static MiscellaneousRetrieval miscellaneousRetrieval;
	
	static CacheRetrieval<Annotation> annotationCacheRetrieval;
	static AnnotationRetrieval annotationRetrieval;
	
	public static void main(String[] args) {

		// Go terms
		cacheGOTerms();
		
		// Miscellaneous
		cacheMiscellaneous();
		
		//Annotations
		cacheAnnotation();
	}

	/**
	 * GO terms cache
	 */
	private static void cacheGOTerms(){
		appContext = new ClassPathXmlApplicationContext("common-beans.xml","query-beans.xml");
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
	 * Miscellaneous data cache
	 */
	private static void cacheMiscellaneous(){
		appContext = new ClassPathXmlApplicationContext("common-beans.xml","query-beans.xml");
		miscellaneousCacheRetrieval = (CacheRetrieval<Miscellaneous>) appContext.getBean("miscellaneousCacheRetrieval");
		miscellaneousRetrieval = (MiscellaneousRetrieval) appContext.getBean("miscellaneousRetrieval");
		
		// Get taxonomies information
		getTaxonomy(2);
		getTaxonomy(6);
	}
	
	
	/**
	 * Annotations data cache
	 */
	private static void cacheAnnotation(){
		appContext = new ClassPathXmlApplicationContext("common-beans.xml","query-beans.xml");
		annotationCacheRetrieval = (CacheRetrieval<Annotation>) appContext.getBean("annotationCacheRetrieval");
		annotationRetrieval = (AnnotationRetrieval) appContext.getBean("annotationRetrieval");
		
		// Get annotations
		getAnnotation("6232720756");		
	}
	
	
	/**
	 * Get an annotation from Solr using the id
	 * @param id
	 */
	private static void getAnnotation(String id) {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			Annotation annotation = annotationCacheRetrieval.retrieveEntry(id, Annotation.class);
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Annotation DATE: " + annotation.getDate());
			System.out.println("Annotation DB Object ID: " + annotation.getDbObjectID());
			System.out.println("Annotation DB: " + annotation.getDb());
			System.out.println("Annotation ECO Id: " + annotation.getEcoID());
			System.out.println("Annotation GO Aspect: " + annotation.getGoAspect());
			System.out.println("Annotation GO ID: " + annotation.getGoID());
			System.out.println("Annotation GO Evidence: " + annotation.getGoEvidence());
			System.out.println("Annotation #With: " + annotation.getWith().size());
			System.out.println("Time (ms) getting information from SolR: "
					+ estimatedTime);

			// Second time it's already cached
			startTime = System.currentTimeMillis();
			annotation = annotationCacheRetrieval.retrieveEntry(id, Annotation.class);
			estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time (ms) getting information from in-memory cache: "
					+ estimatedTime);
			System.out.println("====================");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a taxonomy from Solr using the id
	 * @param id
	 */
	private static void getTaxonomy(int id) {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			Miscellaneous miscellaneous = miscellaneousCacheRetrieval.retrieveEntry(String.valueOf(id), Miscellaneous.class);
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Tax ID: " + miscellaneous.getTaxonomyId());
			System.out.println("Tax NAME: " + miscellaneous.getTaxonomyName());
			System.out.println("Tax Closure: " + miscellaneous.getTaxonomyClosure().size());
			System.out.println("Time (ms) getting information from SolR: "
					+ estimatedTime);

			// Second time it's already cached
			startTime = System.currentTimeMillis();
			miscellaneous = miscellaneousCacheRetrieval.retrieveEntry(String.valueOf(id), Miscellaneous.class);
			estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time (ms) getting information from in-memory cache: "
					+ estimatedTime);
			System.out.println("====================");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
