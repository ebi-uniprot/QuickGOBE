package uk.ac.ebi.quickgo.cache.query.service.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;

/**
 * Some integration tests to test the annotations in-memory cache and SoLR
 * 
 * @author cbonill
 * 
 */
public class AnnotationCacheRetrievalIntegrationTest {

	static ApplicationContext appContext;
	static CacheRetrieval<Annotation> annotationCacheRetrieval;
	static AnnotationRetrieval annotationRetrieval;

	/**
	 * Miscellaneous data cache
	 */
	public static void main(String[] args) {
		appContext = new ClassPathXmlApplicationContext("common-beans.xml","query-beans.xml");
		annotationCacheRetrieval = (CacheRetrieval<Annotation>) appContext.getBean("annotationCacheRetrieval");
		annotationRetrieval = (AnnotationRetrieval) appContext.getBean("annotationRetrieval");
		
		// Get annotations
		getAnnotation("7510100309");	

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
}
