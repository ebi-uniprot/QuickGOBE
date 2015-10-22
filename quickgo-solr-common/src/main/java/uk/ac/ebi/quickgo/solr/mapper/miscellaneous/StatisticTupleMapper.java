package uk.ac.ebi.quickgo.solr.mapper.miscellaneous;

import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.model.statistics.StatisticTuple;

import java.util.Collection;

/**
 * @Author Tony Wardell
 * Date: 22/10/2015
 * Time: 14:29
 * Created with IntelliJ IDEA.
 */
public class StatisticTupleMapper {

	public SolrMiscellaneous mapPrecalculatedStats(StatisticTuple statisticTuple){
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneous.SolrMiscellaneousDocumentType.PRECALCULATED_STATS.getValue());
		solrMiscellaneous.setStatisticTupleType(statisticTuple.getStatisticTupleType());
		solrMiscellaneous.setStatisticTupleKey(statisticTuple.getstatisticTupleKey());
		solrMiscellaneous.setStatisticTupleHits(statisticTuple.getStatisticTupleHits());
		return solrMiscellaneous;
	}
}
