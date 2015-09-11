package uk.ac.ebi.quickgo.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

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

		Properties defaultProps = new Properties();
		try {
			String configDir = System.getProperty("CONFIG");
			File propsFile = new File(configDir, "quickgo-indexing.properties");
			FileInputStream in = new FileInputStream(propsFile);
			defaultProps.load(in);
			in.close();
			quickGOIndexer = (QuickGOIndexer) appContext.getBean("quickGOIndexer");
			quickGOIndexer.setProperties(defaultProps);

			String start = DateFormat.getInstance().format(Calendar.getInstance().getTime());
			logger.info("================================================================");
			logger.info("STARTED: " + start);
			logger.info("================================================================");
			quickGOIndexer.index();
			logger.info("================================================================");
			logger.info("DONE: " + DateFormat.getInstance().format(Calendar.getInstance().getTime()));
			logger.info("================================================================");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
