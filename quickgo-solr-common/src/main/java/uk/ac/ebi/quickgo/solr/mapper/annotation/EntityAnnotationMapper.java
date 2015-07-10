package uk.ac.ebi.quickgo.solr.mapper.annotation;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;

/**
 * To convert Solr annotations into "QuickGO" Annotations
 * @author cbonill
 */
public class EntityAnnotationMapper implements EntityMapper<GOAnnotation, GOAnnotation>{

	@Override
	public GOAnnotation toEntityObject(Collection<GOAnnotation> solrObjects) {
		return toEntityObject(solrObjects, GOAnnotation.SolrAnnotationDocumentType.getAsInterfaces());
	}

	@Override
	public GOAnnotation toEntityObject(Collection<GOAnnotation> solrObjects, List<SolrDocumentType> solrDocumentTypes) {
		for (SolrDocumentType documentType : solrDocumentTypes) {
			if (documentType == GOAnnotation.SolrAnnotationDocumentType.ANNOTATION) {
				return (GOAnnotation)solrObjects.toArray()[0];
			}
		}
		return null;
	}
}
