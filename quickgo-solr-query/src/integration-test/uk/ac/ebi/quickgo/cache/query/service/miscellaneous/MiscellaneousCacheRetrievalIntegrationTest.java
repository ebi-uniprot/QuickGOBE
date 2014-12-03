package uk.ac.ebi.quickgo.cache.query.service.miscellaneous;

import java.util.List;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrieval;

/**
 * Some integration tests to test the miscellaneous in-memory cache and SoLR
 * 
 * @author cbonill
 * 
 */
public class MiscellaneousCacheRetrievalIntegrationTest {

	static ApplicationContext appContext;
	static CacheRetrieval<Miscellaneous> miscellaneousCacheRetrieval;
	static MiscellaneousRetrieval miscellaneousRetrieval;

	/**
	 * Miscellaneous data cache
	 */
	public static void main(String[] args) {
		appContext = new ClassPathXmlApplicationContext("common-beans.xml",	"query-beans.xml");
		miscellaneousCacheRetrieval = (CacheRetrieval<Miscellaneous>) appContext.getBean("miscellaneousCacheRetrieval");
		miscellaneousRetrieval = (MiscellaneousRetrieval) appContext.getBean("miscellaneousRetrieval");

		// Get taxonomies information
		getTaxonomy(2);
		getTaxonomy(6);
		getTaxonomy(9);
		
		// Get sequences
		getSequence("A7HE40");
		
		//Get publications
		getPublication(10076124);
		
		//Annotation guidelines
		getGuideline("GO:0016866");
		
		//Annotation blacklist
		getBlacklist("GO:0005730");
		
		//Annotation Extension Relation
		getAnnotationExtension();
	}

	/**
	 * Get a taxonomy from Solr using the id
	 * 
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
			System.out.println("Tax Closure: "
					+ miscellaneous.getTaxonomyClosure().size());
			System.out.println("Time (ms) getting information from SolR: "
					+ estimatedTime);

			// Second time it's already cached
			startTime = System.currentTimeMillis();
			miscellaneous = miscellaneousCacheRetrieval.retrieveEntry(
					String.valueOf(id), Miscellaneous.class);
			estimatedTime = System.currentTimeMillis() - startTime;
			System.out
					.println("Time (ms) getting information from in-memory cache: "
							+ estimatedTime);
			System.out.println("====================");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a sequence from Solr using the accession
	 * 
	 * @param id
	 */
	private static void getSequence(String accesion) {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			Miscellaneous miscellaneous = miscellaneousRetrieval.findByMiscellaneousId(accesion, MiscellaneousField.DBOBJECTID.getValue());
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Accession ID: " + miscellaneous.getDbObjectID());
			System.out.println("Sequence: " + miscellaneous.getSequence());			
			System.out.println("Time (ms) getting information from Solr: " + estimatedTime);
			System.out.println("====================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get publication from Solr
	 * @param publicationId Publication ID
	 */
	private static void getPublication(int publicationId) {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			Miscellaneous miscellaneous = miscellaneousRetrieval.findByMiscellaneousId(String.valueOf(publicationId), MiscellaneousField.PUBLICATIONID.getValue());
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Publication ID: " + miscellaneous.getPublicationID());
			System.out.println("Publication title: " + miscellaneous.getPublicationTitle());			
			System.out.println("Time (ms) getting information from Solr: " + estimatedTime);
			System.out.println("====================");
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Get annotation guideline from Solr
	 * @param publicationId Publication ID
	 */
	private static void getGuideline(String termID) {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			List<Miscellaneous> miscellaneousList = miscellaneousRetrieval
					.findByQuery(MiscellaneousField.TERM.getValue()
							+ ":"
							+ ClientUtils.escapeQueryChars(termID)
							+ " AND "
							+ MiscellaneousField.TYPE.getValue()
							+ ":"
							+ SolrMiscellaneousDocumentType.GUIDELINE
									.getValue(), -1);
			long estimatedTime = System.currentTimeMillis() - startTime;
			for(Miscellaneous miscellaneous : miscellaneousList){
				System.out.println("Guideline term: " + miscellaneous.getTerm());
				System.out.println("Guideline title: " + miscellaneous.getGuidelineTitle());			
				System.out.println("Guideline URL: " + miscellaneous.getGuidelineURL());
				System.out.println("Time (ms) getting information from Solr: " + estimatedTime);
				System.out.println("====================");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Get annotation blacklist from Solr
	 * @param termID GOTerm ID
	 */
	private static void getBlacklist(String termID) {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			List<Miscellaneous> miscellaneousList = miscellaneousRetrieval
					.findByQuery(MiscellaneousField.TERM.getValue()
							+ ":"
							+ ClientUtils.escapeQueryChars(termID)
							+ " AND "
							+ MiscellaneousField.TYPE.getValue()
							+ ":"
							+ SolrMiscellaneousDocumentType.BLACKLIST
									.getValue(), -1);
			long estimatedTime = System.currentTimeMillis() - startTime;
			for(Miscellaneous miscellaneous : miscellaneousList){
				System.out.println("Backlist term: " + miscellaneous.getTerm());
				System.out.println("Blacklist protein: " + miscellaneous.getDbObjectID());			
				System.out.println("Blacklist Reason: " + miscellaneous.getBacklistReason());
				System.out.println("Blacklist Category: " + miscellaneous.getBacklistCategory());
				System.out.println("Blacklist Entry type: " + miscellaneous.getBacklistEntryType());
				System.out.println("Time (ms) getting information from Solr: " + estimatedTime);
				System.out.println("====================");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Get annotation extension relation from Solr
	 * @param publicationId Publication ID
	 */
	private static void getAnnotationExtension() {
		try {
			// First time the entry is gotten from SolR
			long startTime = System.currentTimeMillis();
			List<Miscellaneous> miscellaneousList = miscellaneousRetrieval
					.findByQuery(MiscellaneousField.TYPE.getValue()
							+ ":"
							+ SolrMiscellaneousDocumentType.EXTENSION.getValue(), -1);
			long estimatedTime = System.currentTimeMillis() - startTime;
			for(Miscellaneous miscellaneous : miscellaneousList){
				System.out.println("AER name: " + miscellaneous.getAerName());
				System.out.println("AER domain: " + miscellaneous.getAerDomain());			
				System.out.println("AER usage: " + miscellaneous.getAerUsage());
				System.out.println("Time (ms) getting information from Solr: " + estimatedTime);
				System.out.println("====================");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}