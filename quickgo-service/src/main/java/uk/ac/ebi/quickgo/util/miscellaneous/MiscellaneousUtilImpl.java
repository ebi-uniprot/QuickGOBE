package uk.ac.ebi.quickgo.util.miscellaneous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrieval;

/**
 * Useful miscellaneous operations not published in Miscellaneous service
 * 
 * @author cbonill
 * 
 */
@Service
public class MiscellaneousUtilImpl implements MiscellaneousUtil {

	// Log
	private static final Logger logger = Logger.getLogger(MiscellaneousUtil.class);

	@Autowired
	MiscellaneousRetrieval miscellaneousRetrieval;

	/**
	 * See {@link MiscellaneousUtil#getDBInformation(String)}
	 */
	@Cacheable("xrefDbInformation")
	public Miscellaneous getDBInformation(String abbreviation) {
		List<Miscellaneous> dbs = new ArrayList<>();
		try {
			dbs = miscellaneousRetrieval.findByQuery(
					MiscellaneousField.TYPE.getValue() + ":"
							+ SolrMiscellaneousDocumentType.XREFDB.getValue()
							+ " AND "
							+ MiscellaneousField.XREFABBREVIATION.getValue()
							+ ":" + abbreviation, 1);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		if (dbs.size() > 0) {
			return dbs.get(0);
		}
		return new Miscellaneous();
	}

	/**
	 * See {@link MiscellaneousUtil#getSubsetCount(List)}
	 */
	@Cacheable("subsetCount")
	public List<Miscellaneous> getSubsetCount(List<String> subsetsString) {
		List<Miscellaneous> subsetsCounts = new ArrayList<>();
		List<Miscellaneous> subsets = new ArrayList<>();
		try {
			if (subsetsString == null) {// Get all of them
				subsets = miscellaneousRetrieval.findByQuery(
						MiscellaneousField.TYPE.getValue()
								+ ":"
								+ SolrMiscellaneousDocumentType.SUBSETCOUNT
										.getValue(), 10);
				subsetsCounts.addAll(subsets);
			} else {
		
				for (String subset : subsetsString) {
					subsets = miscellaneousRetrieval.findByQuery(
							MiscellaneousField.TYPE.getValue()
									+ ":"
									+ SolrMiscellaneousDocumentType.SUBSETCOUNT
											.getValue() + " AND "
									+ MiscellaneousField.SUBSET.getValue()
									+ ":" + subset, 1);
					subsetsCounts.add(subsets.get(0));
				}
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}

		return subsetsCounts;
	}

	/**
	 * See {@link MiscellaneousUtil#getTaxonomiesNames(List)}
	 */
	@Override
	@Cacheable("taxonNames")
	public Map<String, String> getTaxonomiesNames(List<String> taxonomiesIds) {
		Map<String, String> taxIdName = new HashMap<>();
		Map<String, Map<String, String>> taxonomies = new HashMap<>();
		try {
			taxonomies = miscellaneousRetrieval
					.getFieldValues(
							MiscellaneousField.TYPE.getValue()
									+ ":"
									+ SolrMiscellaneousDocumentType.TAXONOMY
											.getValue()
									+ " AND "
									+ MiscellaneousField.TAXONOMY_ID.getValue()
									+ ":("
									+ StringUtils.arrayToDelimitedString(
											taxonomiesIds.toArray(), " OR ") + ")",
							MiscellaneousField.TAXONOMY_ID.getValue(),
							MiscellaneousField.TAXONOMY_NAME.getValue());
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		for (String key : taxonomies.keySet()) {
			String name = taxonomies.get(key).get(MiscellaneousField.TAXONOMY_NAME.getValue());
			taxIdName.put(key, name);
		}
		return taxIdName;
	}
	
	/**
	 * See {@link MiscellaneousUtil#getEvidenceTypes()}
	 */
	public Map<String, String> getEvidenceTypes(){
		Map<String, String> evidenceTypes = new HashMap<>();
		try {
			List<Miscellaneous> evidences = miscellaneousRetrieval
					.findByQuery(
							MiscellaneousField.TYPE.getValue()
							+ ":" + SolrMiscellaneousDocumentType.EVIDENCE.getValue(), 100);
			for(Miscellaneous evidence : evidences){
				evidenceTypes.put(evidence.getEvidenceCode(), evidence.getEvidenceName());
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return evidenceTypes;
	}
}