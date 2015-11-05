package uk.ac.ebi.quickgo.indexer.statistics;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.indexing.service.miscellaneous.MiscellaneousIndexer;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.mapper.miscellaneous.SolrMiscellaneousMapper;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrievalImpl;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessorImpl;

/**
 * @Author Tony Wardell
 * Date: 15/09/2015
 * Time: 14:38
 * Created with IntelliJ IDEA.
 */
public class QuickGOCOOccurrenceStatsIndexerIT {

	private static final String ANNOTATION_SOLR_URL = "http://ves-hx-cf.ebi.ac.uk:8090/Solr/annotation";
	private static final String MISC_SOLR_URL = "http://ves-hx-cf.ebi.ac.uk:8090/Solr/miscellaneous";

	public static void main(String[] args) {

		QuickGOCOOccurrenceStatsIndexer quickGOCOOccurrenceStatsIndexer = new QuickGOCOOccurrenceStatsIndexer();

		//Setup talking to the solr server for annotations
		SolrServerProcessorImpl annotationServerProcessor = new SolrServerProcessorImpl();
		annotationServerProcessor.setSolrURL(ANNOTATION_SOLR_URL);

		//Set up an object to retrieve the annotations from solr
		AnnotationRetrievalImpl annotationRetrieval  = new  AnnotationRetrievalImpl();
		annotationRetrieval.setAnnotationServerProcessor(annotationServerProcessor);

		//Setup talking to the solr server for miscellaneous
		SolrServerProcessorImpl miscServerProcessor = new SolrServerProcessorImpl();
		miscServerProcessor.setSolrURL(MISC_SOLR_URL);

		SolrMapper<Miscellaneous, SolrMiscellaneous> miscMapper = new SolrMiscellaneousMapper();


		//Setup an object to publish the stats calculations too
		MiscellaneousIndexer miscellaneousIndexer = new MiscellaneousIndexer();
		miscellaneousIndexer.setSolrServerProcessor(miscServerProcessor);
		miscellaneousIndexer.setSolrMapper(miscMapper);


		//Set up an object to index the stats
		quickGOCOOccurrenceStatsIndexer.annotationRetrieval = annotationRetrieval;
		quickGOCOOccurrenceStatsIndexer.miscellaneousIndexer = miscellaneousIndexer;

		//quickGOCOOccurrenceStatsIndexer.setProperties(this.properties);		// isn't used
		quickGOCOOccurrenceStatsIndexer.index();


	}

}
