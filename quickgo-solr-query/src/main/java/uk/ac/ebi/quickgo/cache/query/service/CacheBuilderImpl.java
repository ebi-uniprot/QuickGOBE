package uk.ac.ebi.quickgo.cache.query.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;

/**
 * In-memory cache builder implementation
 * 
 * @author cbonill
 * 
 * @param <T>
 */
@Service("cacheBuilder")
public class CacheBuilderImpl<T> implements CacheBuilder<T> {

	Retrieval<T> retrieval;

	// Contains the cached values
	Map<String, T> cachedValues = new HashMap<String, T>(30000);

	/**
	 * See {@link CacheBuilder#addEntry(String, Class)}  
	 */
	public T addEntry(String id, Class<T> type) throws NotFoundException, SolrServerException {
		// Try to get if from the cache
		T value = cachedValues.get(id);		

		if (value == null) {// It's not cached yet
			// Get it from Solr
			value = retrieval.findById(id);			
			if (value == null) {// It's not in Solr either
				throw new NotFoundException("Entry with ID: " + id + " not found in Solr");
			}			
			// Add it into memory
			cachedValues.put(id, value);			
		}
		return value;
	}

	/**
	 * See {@link CacheBuilder#addEntry(String, T)}  
	 */
	public void addEntry(String id, T object) {
		if (!cachedValues.containsKey(id)) {
			cachedValues.put(id, object);
		}
	}
	
	/**
	 * See {@link CacheBuilder#cachedValue(String)}
	 */
	public T cachedValue(String id) {
		return cachedValues.get(id);
	}

	public Retrieval<T> getRetrieval() {
		return retrieval;
	}

	public void setRetrieval(Retrieval<T> retrieval) {
		this.retrieval = retrieval;
	}

	public Map<String, T> getCachedValues() {
		return cachedValues;
	}

	@Override
	public void clearCache() {
		this.cachedValues.clear();		
	}
}