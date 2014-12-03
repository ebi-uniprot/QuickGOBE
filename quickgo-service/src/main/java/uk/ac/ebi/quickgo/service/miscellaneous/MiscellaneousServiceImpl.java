package uk.ac.ebi.quickgo.service.miscellaneous;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousValue;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrieval;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

/**
 * Miscellaneous service implementation
 * @author cbonill
 *
 */
public class MiscellaneousServiceImpl implements MiscellaneousService{

	// Log
	private static final Logger logger = Logger.getLogger(MiscellaneousServiceImpl.class);
		
	MiscellaneousRetrieval miscellaneousRetrieval;
	
	@Override
	public Map<String, Map<String, String>> retrieveTaxonomiesNames() {
		Map<String, Map<String, String>> values = new HashMap<String, Map<String,String>>();
		try {
			values = miscellaneousRetrieval.getFieldValues(MiscellaneousField.TYPE.getValue()
					+ ":" + SolrMiscellaneousDocumentType.TAXONOMY.getValue(),
					MiscellaneousField.TAXONOMY_ID.getValue(), MiscellaneousField.TAXONOMY_NAME.getValue());
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return values;
	}
	
	public List<Miscellaneous> getWithDBs(){
		List<Miscellaneous> withDbs = null;
		try {
			withDbs = miscellaneousRetrieval.findByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.XREFDB.getValue(), 1000);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return withDbs;
	}
	

	@Override
	public List<Miscellaneous> getBlacklist() {
		List<Miscellaneous> blacklist = null;
		try {
			blacklist = miscellaneousRetrieval.findByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.BLACKLIST.getValue(), 10000);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return blacklist;
	}	
	
	public List<Miscellaneous> getPostProcessingRules(){
		List<Miscellaneous> ppr = null;
		try {
			ppr = miscellaneousRetrieval.findByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.POSTPROCESSINGRULE.getValue(), 1000);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return ppr;
	}
	
	@Override
	public Set<COOccurrenceStatsTerm> allCOOccurrenceStatistics(String ontologyTermID) {
		return getCOOccurrenceStatistics(ontologyTermID, MiscellaneousValue.ALL.getValue());
	}

	@Override
	public Set<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics(	String ontologyTermID) {
		return getCOOccurrenceStatistics(ontologyTermID, MiscellaneousValue.NON_IEA.getValue());
	}
	
	private Set<COOccurrenceStatsTerm> getCOOccurrenceStatistics(String ontologyTermID, String type){
		TreeSet<COOccurrenceStatsTerm> coOccurrenceStatsTerms = new TreeSet<COOccurrenceStatsTerm>();
		List<Miscellaneous> allStats = null;
		try {
			allStats = miscellaneousRetrieval.findByQuery(
					MiscellaneousField.TERM.getValue() + ":"
							+ ClientUtils.escapeQueryChars(ontologyTermID)
							+ " AND " + MiscellaneousField.TYPE.getValue()
							+ ":" + SolrMiscellaneousDocumentType.STATS.getValue() + " AND "
							+ MiscellaneousField.STATS_TYPE.getValue() + ":"
							+ type, Integer.MAX_VALUE);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		for(Miscellaneous miscellaneous : allStats){
			coOccurrenceStatsTerms.add(new COOccurrenceStatsTerm(miscellaneous));
		}
		return coOccurrenceStatsTerms.descendingSet();
	}

	public MiscellaneousRetrieval getMiscellaneousRetrieval() {
		return miscellaneousRetrieval;
	}

	public void setMiscellaneousRetrieval(
			MiscellaneousRetrieval miscellaneousRetrieval) {
		this.miscellaneousRetrieval = miscellaneousRetrieval;
	}
}