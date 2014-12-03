package uk.ac.ebi.quickgo.cache.query.service.annotation;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;

/**
 * Integration tests for {@link AnnotationRetrieval}
 * @author cbonill
 *
 */
public class AnnotationRetrievalIntegrationTest {

	static ApplicationContext appContext;
	static CacheRetrieval<Annotation> annotationCacheRetrieval;
	static AnnotationRetrieval annotationRetrieval;

	/**
	 * Miscellaneous data cache
	 */
	public static void main(String[] args) {
		appContext = new ClassPathXmlApplicationContext("common-beans.xml","query-beans.xml");
		annotationRetrieval = (AnnotationRetrieval) appContext.getBean("annotationRetrieval");	
		
		// Get 10 GO top terms
		try {
			getTopTerms();
		} catch (SolrServerException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Get 10 GO top terms assigned by InterPro
	 * @throws SolrServerException
	 */
	private static void getTopTerms() throws SolrServerException{
		
		List<Term> terms = annotationRetrieval.getTopTerms("goID", 10);

		for (Term term : terms) {
			System.out.println(term.getTerm() + ": " + term.getFrequency());
		}
	}
}
