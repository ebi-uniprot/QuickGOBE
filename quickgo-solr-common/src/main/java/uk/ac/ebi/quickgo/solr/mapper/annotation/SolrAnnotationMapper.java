package uk.ac.ebi.quickgo.solr.mapper.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;

/**
 * To map Annotations entities to Solr ones
 * @author cbonill
 *
 */
@Service("solrAnnotationMapper")
public class SolrAnnotationMapper implements SolrMapper<GOAnnotation, GOAnnotation>{

	@Override
	public Collection<GOAnnotation> toSolrObject(GOAnnotation genericObject) {
		return toSolrObject(genericObject, GOAnnotation.SolrAnnotationDocumentType.getAsInterfaces());
	}

	@Override
	public Collection<GOAnnotation> toSolrObject(GOAnnotation annotation, List<SolrDocumentType> solrDocumentTypes) {
		List<GOAnnotation> annotations = new ArrayList<>();

		for (SolrDocumentType documentType : solrDocumentTypes) {
			if (documentType == GOAnnotation.SolrAnnotationDocumentType.ANNOTATION) {
				annotations.add(mapBasicInformation(annotation));
			}
		}
		return annotations;
	}

	/**
	 * Maps annotation basic information
	 * @param annotation Annotation to map to Solr object
	 * @return Solr annotation representation
	 */
	private GOAnnotation mapBasicInformation(GOAnnotation annotation) {
		annotation.setDocType(GOAnnotation.SolrAnnotationDocumentType.ANNOTATION.getValue());
		return annotation;
	}
}
