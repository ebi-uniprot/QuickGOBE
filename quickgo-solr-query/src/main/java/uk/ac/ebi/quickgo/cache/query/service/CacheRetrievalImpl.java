package uk.ac.ebi.quickgo.cache.query.service;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.solr.exception.NotFoundException;

/**
 * Cache retrieval implementation
 * @author cbonill
 *
 * @param <T>
 */
@Service("cacheRetrieval")
public class CacheRetrievalImpl<T> implements CacheRetrieval<T> {

	CacheBuilder<T> cacheBuilder;
	
	// Log
	private static final Logger logger = Logger.getLogger(CacheRetrievalImpl.class);
	
	/**
	 * See {@link CacheRetrieval#retrieveEntry(String, Class)
	 * @throws NotFoundException Thrown if the entry is not found
	 */
	public T retrieveEntry(String id, Class<T> type) throws NotFoundException {
		// Try to get if from in-memory cache
		T entry = cacheBuilder.cachedValue(id);
		// If not, add it from SolR
		if(entry == null){
			try {
				return cacheBuilder.addEntry(id, type);			
			} catch (SolrServerException e) {
				logger.error(e.getMessage());				
			}
		}
		return entry;
	}

	public void setCacheBuilder(CacheBuilder<T> cacheBuilder) {
		this.cacheBuilder = cacheBuilder;
	}

	public CacheBuilder<T> getCacheBuilder() {
		return cacheBuilder;
	}
}