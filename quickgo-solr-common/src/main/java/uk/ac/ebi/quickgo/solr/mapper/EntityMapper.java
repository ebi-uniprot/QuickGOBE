package uk.ac.ebi.quickgo.solr.mapper;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Map Solr information into Entity objects for QuickGO application
 * @author cbonill
 *
 * @param <V> Solr object type to map (source)
 * @param <T> Entity object (target) 
 */
public interface EntityMapper<V,T> {
	/**
	 * Map all the Solr information from a Set of Solr terms into an Entity object 
	 * @param solrObjects Solr objects to map
	 * @return QuickGO entity object
	 */
	T toEntityObject(Collection<V> solrObjects);
		
	/**
	 * Map Solr information from a Set of Solr terms into an Entity object for the selected Solr fields 
	 * @param solrObjects Solr objects to map
	 * @param solrDocumentTypes Document types to map
	 * @return QuickGO entity object
	 */
	T toEntityObject (Collection<V> solrObjects, List<SolrDocumentType> solrDocumentTypes);
}
