package uk.ac.ebi.quickgo.cache.query.service.geneproduct;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.query.service.geneproduct.GeneProductRetrieval;

/**
 * Integration test for GeneProductRetrieval
 * @author cbonill
 *
 */
public class GeneProductRetrievalIntegrationTest {

	static ApplicationContext appContext;
	static GeneProductRetrieval geneProductRetrieval;

	public static void main(String[] args) {

		appContext = new ClassPathXmlApplicationContext("common-beans.xml", "query-beans.xml");
		geneProductRetrieval = (GeneProductRetrieval) appContext.getBean("geneProductRetrieval");

		List<GeneProduct> allGPs = new ArrayList<>();

		// Find by name
		long startTime = System.currentTimeMillis();
		try {
			allGPs = geneProductRetrieval.findByName("*fun*");
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time (ms) building Gene Products Basic Information with name *fun*: " + estimatedTime);
		System.out.println("Number Gene Products retrieved:" + allGPs.size());
						
		
		// Find by id
		GeneProduct geneProduct = null;
		startTime = System.currentTimeMillis();
		try {
			geneProduct = geneProductRetrieval.findById("P30994");
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("=================================");
		System.out.println("Time (ms) building Gene Products Basic Information with id P30994: " + estimatedTime);
		System.out.println("Gene Product ID:" + geneProduct.getDbObjectId());
		System.out.println("Gene Product Name:" + geneProduct.getDbObjectName());
		System.out.println("#Gene Product Properties: " + geneProduct.getGeneProductProperties().size());
		System.out.println("#Gene Product Synonyms: " + geneProduct.getXRefs().size());
	}

}
