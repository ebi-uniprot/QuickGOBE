package uk.ac.ebi.quickgo.indexer.statistics;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.indexing.service.miscellaneous.MiscellaneousIndexer;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.mapper.miscellaneous.SolrMiscellaneousMapper;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrievalImpl;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessorImpl;

/**
 * @Author Tony Wardell
 * Date: 15/09/2015
 * Time: 14:38
 * Created with IntelliJ IDEA.
 */
public class QuickGOCOOccurrenceStatsIndexerTest {

	//private static final String ANNOTATION_SOLR_URL = "http://ves-hx-cf.ebi.ac.uk:8090/Solr/annotation";
	//private static final String MISC_SOLR_URL = "http://ves-hx-cf.ebi.ac.uk:8090/Solr/miscellaneous";

	@Test
	public void test()  throws Exception{

		QuickGOCOOccurrenceStatsIndexer quickGOCOOccurrenceStatsIndexer = new QuickGOCOOccurrenceStatsIndexer();

//		//Setup talking to the solr server for annotations
//		SolrServerProcessorImpl annotationServerProcessor = new SolrServerProcessorImpl();
//		annotationServerProcessor.setSolrURL(ANNOTATION_SOLR_URL);
//
//		//Set up an object to retrieve the annotations from solr
//		AnnotationRetrievalImpl annotationRetrieval  = new  AnnotationRetrievalImpl();
//		annotationRetrieval.setAnnotationServerProcessor(annotationServerProcessor);
//
//		//Setup talking to the solr server for miscellaneous
//		SolrServerProcessorImpl miscServerProcessor = new SolrServerProcessorImpl();
//		miscServerProcessor.setSolrURL(MISC_SOLR_URL);
//
//		SolrMapper<Miscellaneous, SolrMiscellaneous> miscMapper = new SolrMiscellaneousMapper();
//
//
//		//Setup an object to publish the stats calculations too
//		MiscellaneousIndexer miscellaneousIndexer = new MiscellaneousIndexer();
//		miscellaneousIndexer.setSolrServerProcessor(miscServerProcessor);
//		miscellaneousIndexer.setSolrMapper(miscMapper);


		Mockery context = new Mockery();
		AnnotationRetrieval annotationRetrieval = context.mock(AnnotationRetrieval.class);
		Indexer<Miscellaneous> miscellaneousIndexer = context.mock(Indexer.class);

		context.checking(new Expectations() {
			{
				allowing(miscellaneousIndexer).deleteByQuery("docType:stats");

				allowing(annotationRetrieval).getTotalNumberProteins("*:*");
				will(returnValue(100l));

				allowing(annotationRetrieval).getTotalNumberProteins("NOT goEvidence:IEA");
				will(returnValue(90l));

			}
		});

		//Set up an object to index the stats
		quickGOCOOccurrenceStatsIndexer.annotationRetrieval = annotationRetrieval;
		quickGOCOOccurrenceStatsIndexer.miscellaneousIndexer = miscellaneousIndexer;

		quickGOCOOccurrenceStatsIndexer.setProperties(null);  //doesn't appear to be used
		quickGOCOOccurrenceStatsIndexer.index();



	}

}
