package uk.ac.ebi.quickgo.indexer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Process to index all the information in Solr
 *
 * @author cbonill
 */
public class QuickGOIndexerProcess {


	static ApplicationContext appContext;
	static QuickGOIndexer quickGOIndexer;

	final static Logger logger = LoggerFactory.getLogger(QuickGOIndexerProcess.class);

	/**
	 * We can use this method for indexing the data in Solr for the moment
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		appContext = new ClassPathXmlApplicationContext("common-beans.xml", "indexing-beans.xml", "query-beans.xml");
		quickGOIndexer = (QuickGOIndexer) appContext.getBean("quickGOIndexer");
		String start = DateFormat.getInstance().format(Calendar.getInstance().getTime());
		logger.info("================================================================");
		logger.info("STARTED: " + start);
		logger.info("================================================================");
		quickGOIndexer.index();
		logger.info("================================================================");
		logger.info("DONE: " + DateFormat.getInstance().format(Calendar.getInstance().getTime()));
		logger.info("================================================================");
	}
}
