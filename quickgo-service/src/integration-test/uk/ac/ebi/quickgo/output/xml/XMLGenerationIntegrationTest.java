package uk.ac.ebi.quickgo.output.xml;

import javax.xml.bind.JAXBException;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.cache.query.service.CacheRetrieval;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.output.EntityToStream;
import uk.ac.ebi.quickgo.render.Format;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrieval;

/**
 * Integration test for XML Generation
 * @author cbonill
 *
 */
public class XMLGenerationIntegrationTest {


	static ApplicationContext appContext;
	static CacheRetrieval<GOTerm> cacheRetrieval;
	static TermRetrieval termRetrieval;
	static final Logger logger = LoggerFactory.getLogger(XMLGenerationIntegrationTest.class)

	public static void main(String[] args) throws JAXBException{

		appContext = new ClassPathXmlApplicationContext("service-beans.xml","common-beans.xml","query-beans.xml");

		EntityToStream<GOTerm> termEntityToStream = (EntityToStream<GOTerm>) appContext.getBean("goTermEntityToStream");
		cacheRetrieval = (CacheRetrieval<GOTerm>) appContext.getBean("goTermCacheRetrieval");
		termRetrieval = (TermRetrieval) appContext.getBean("termRetrieval");
		// Get basic information for a few entries and convert them to XML
		GOTerm goTerm = getEntry("GO:0005458");
		termEntityToStream.convertToXMLStream(goTerm, Format.XML, System.out);

		goTerm = getEntry("GO:0043066");
		termEntityToStream.convertToXMLStream(goTerm, Format.XML, System.out);
	}

	/**
	 * Get an entry from Solr and once it's cached, get it from the in-memory cache
	 * @param entry Entry to retrieve
	 */
	private static GOTerm getEntry(String entry) {

		try {
			// First time the entry is gotten from Solr
			long startTime = System.currentTimeMillis();
			GOTerm term = cacheRetrieval.retrieveEntry(entry, GOTerm.class);
			long estimatedTime = System.currentTimeMillis() - startTime;
			logger.info("Term ID: " + term.getId());
			logger.info("Term NAME: " + term.getName());
			logger.info("Term ONTOLOGY: " + term.getAspect().description);
			logger.info("Time (ms) getting information from Solr: "
					+ estimatedTime);

			// Second time it's already cached
			startTime = System.currentTimeMillis();
			term = cacheRetrieval.retrieveEntry(entry, GOTerm.class);
			estimatedTime = System.currentTimeMillis() - startTime;
			logger.info("Time (ms) getting information from in-memory cache: "
					+ estimatedTime);
			logger.info("====================");
			return term;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

}
