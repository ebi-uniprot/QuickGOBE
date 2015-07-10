package uk.ac.ebi.quickgo.solr.mapper;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Maps generic QuickGO objects into Solr ones
 * @author cbonill
 *
 * @param <T> Generic object type to map (source)
 * @param <V> Solr object type (target)
 */
public interface SolrMapper<T, V> {

	/**
	 * Maps all the Object information to Solr entities
	 * @param genericObject Object to convert into Solr
	 * @return Solr mapped objects
	 */
	Collection<V> toSolrObject(T genericObject);
		
	/**
	 * Maps the selected document types to Solr entites
	 * @param genericObject Object to convert into Solr
	 * @param solrDocumentTypes Document types to map
	 * @return Solr mapped objects
	 */
	Collection<V> toSolrObject (T genericObject, List<SolrDocumentType> solrDocumentTypes);
}
