package uk.ac.ebi.quickgo.solr.query.service.miscellaneous;

import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;

/**
 * Interface for retrieving Miscellaneous data from Solr
 *
 * @author cbonill
 *
 */
public interface MiscellaneousRetrieval extends Retrieval<Miscellaneous>{

	/**
	 * Find by id specifying the miscellaneous type to retrieve (stats, taxonomy, sequence, ...)
	 * @param id Id to retrieve
	 * @param type Type of miscellaneous object
	 * @return Miscellaneous object 
	 * @throws SolrServerException
	 */
	Miscellaneous findByMiscellaneousId(String idValue, String idField) throws SolrServerException;
	
	/**
	 * Return taxonomies identifiers and the specified list of fields
	 * @param fieldID Field id
	 * @param fields Fields to retrieve
	 */
	public  Map<String, Map<String, String>> getFieldValues(String query, String fieldID, String fields) throws SolrServerException;
}
