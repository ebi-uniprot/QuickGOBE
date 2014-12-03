package uk.ac.ebi.quickgo.indexer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Process to index all the information in Solr
 * 
 * @author cbonill
 * 
 */
public class QuickGOIndexerProcess {


		static ApplicationContext appContext;
		static QuickGOIndexer quickGOIndexer;

		/**
		 * We can use this method for indexing the data in Solr for the moment
		 * 
		 * @param args
		 */
		public static void main(String[] args) {

			appContext = new ClassPathXmlApplicationContext("common-beans.xml",	"indexing-beans.xml", "query-beans.xml");
			quickGOIndexer = (QuickGOIndexer) appContext.getBean("quickGOIndexer");
			String start = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			System.out.println("STARTED: " + start);
			quickGOIndexer.index();
			String end = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			System.out.println("DONE: " + end);
		}	
}
