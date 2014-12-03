package uk.ac.ebi.quickgo.solr.indexing;

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
}
