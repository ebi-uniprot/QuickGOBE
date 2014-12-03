package uk.ac.ebi.quickgo.cache.query.service;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.quickgo.solr.exception.NotFoundException;


/**
 * To build an in-memory cache from SolR index
 * 
 * @author cbonill
 * 
 */
public interface CacheBuilder<T> {

	/**
	 * Add an entry to the in-memory cache getting values from SolR
	 * @param id Entry id
	 * @param type Entry type
	 * @return Cached entry
	 * @throws NotFoundException Thrown if the entry doesn't exist in SolR
	 */
	public T addEntry(String id, Class<T> type) throws NotFoundException, SolrServerException;
	
	/**
	 * Add an object directly to the cache 
	 * @param id Object id
	 * @param object Object to be added into the cache 
	 */
	public void addEntry(String id, T object);
	
	/**
	 * Get the entry if it's cached, null otherwise
	 * @param id Entry id
	 * @return The entry if it's cached, null otherwise
	 */
	public T cachedValue(String id);
	
	/**
	 * Delete all the cache content
	 */
	public void clearCache();
}
