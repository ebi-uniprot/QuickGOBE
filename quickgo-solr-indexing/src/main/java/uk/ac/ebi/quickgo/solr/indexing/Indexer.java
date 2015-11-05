package uk.ac.ebi.quickgo.solr.indexing;

import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

/**
 * Interface that must be implemented to index data in Solr
 * @author cbonill
 */
public interface Indexer<T> {

	/**
	 * Indexes a list of objects
	 * @param list List of objects to index
	 */
	public void index(List<T> list);

	public void deleteAll() throws SolrServerException, IOException;

	public void deleteByQuery(String query);
}
