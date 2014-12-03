package uk.ac.ebi.quickgo.cache.query.service;

import uk.ac.ebi.quickgo.solr.exception.NotFoundException;

/**
 * Interface for retrieving values from in-memory cache
 * @author cbonill
 *
 * @param <T>
 */
public interface CacheRetrieval<T> {

	/**
	 * To retrieve an entry from the in-memory cache
	 * @param id Entry id to retrieve
	 * @param type Type of the entry
	 * @return Entry
	 * @throws NotFoundException Thrown if entry doesn't exist in SolR
	 */
	public T retrieveEntry(String id, Class<T> type) throws NotFoundException;	
}