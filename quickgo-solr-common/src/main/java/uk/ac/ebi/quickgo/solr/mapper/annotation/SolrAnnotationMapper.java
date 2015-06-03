package uk.ac.ebi.quickgo.solr.mapper.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation.SolrAnnotationDocumentType;

/**
 * To map Annotations entities to Solr ones
 * @author cbonill
 *
 */
@Service("solrAnnotationMapper")
public class SolrAnnotationMapper implements SolrMapper<Annotation, SolrAnnotation>{

	@Override
	public Collection<SolrAnnotation> toSolrObject(Annotation genericObject) {
		return toSolrObject(genericObject, SolrAnnotationDocumentType.getAsInterfaces());
	}

	@Override
	public Collection<SolrAnnotation> toSolrObject(Annotation annotation,
			List<SolrDocumentType> solrDocumentTypes) {

		List<SolrAnnotation> solrAnnotations = new ArrayList<SolrAnnotation>();

		for (SolrDocumentType annotationDocumentType : solrDocumentTypes) {
			SolrAnnotationDocumentType solrAnnotationDocumentType  = ((SolrAnnotationDocumentType) annotationDocumentType);

			switch (solrAnnotationDocumentType) {

			case ANNOTATION:
				solrAnnotations.add(mapBasicInformation(annotation));
				break;
			}
		}
		return solrAnnotations;
	}

	/**
	 * Maps annotation basic information
	 * @param annotation Annotation to map to Solr object
	 * @return Solr annotation representation
	 */
	private SolrAnnotation mapBasicInformation(Annotation annotation) {
		SolrAnnotation solrAnnotation = new SolrAnnotation();
		solrAnnotation.setDocType(SolrAnnotationDocumentType.ANNOTATION.getValue());
		solrAnnotation.setGoEvidence(annotation.getGoEvidence());
		solrAnnotation.setDb(annotation.getDb());
		solrAnnotation.setDbObjectID(annotation.getDbObjectID());
		solrAnnotation.setDbObjectName(annotation.getDbObjectName());
		solrAnnotation.setDbObjectSymbol(annotation.getDbObjectSymbol());
		solrAnnotation.setDbObjectType(annotation.getDbObjectType());
		solrAnnotation.setDbObjectSynonyms(annotation.getDbObjectSynonyms());
		solrAnnotation.setQualifier(annotation.getQualifier());
		solrAnnotation.setGoID(annotation.getGoID());
		solrAnnotation.setEcoID(annotation.getEcoID());
		solrAnnotation.setTermName(annotation.getTermName());
		solrAnnotation.setDate(annotation.getDate());
		solrAnnotation.setAssignedBy(annotation.getAssignedBy());
		solrAnnotation.setDbXref(annotation.getReference());
		solrAnnotation.setWith(annotation.getWith());
		solrAnnotation.setFullWith(annotation.getFullWith());
		solrAnnotation.setGoAspect(annotation.getGoAspect());
		solrAnnotation.setExtensions(annotation.getExtensions());
		solrAnnotation.setFullExtension(annotation.getFullExtension());
		solrAnnotation.setTaxonomyId(annotation.getTaxonomyId());
		solrAnnotation.setTaxonomyName(annotation.getTaxonomyName());
		solrAnnotation.setTaxonomyClosures(annotation.getTaxonomyClosure());
		solrAnnotation.setAncestorsI(annotation.getAncestorsI());
		solrAnnotation.setAncestorsIPO(annotation.getAncestorsIPO());
		solrAnnotation.setAncestorsIPOR(annotation.getAncestorsIPOR());
		solrAnnotation.setEcoAncestorsI(annotation.getEcoAncestorsI());
		solrAnnotation.setTargetSet(annotation.getTargetSet());
		solrAnnotation.setSequenceLength(annotation.getSequenceLength());
		solrAnnotation.setGp2proteinList(annotation.getGp2protein());
		solrAnnotation.setSubSets(annotation.getSubset());

		if(solrAnnotation.getFullExtension()!=null || solrAnnotation.getFullWith()!=null){
			System.out.println("Have got a Solr Annotation with some fullness " + solrAnnotation.toString());
		}

		return solrAnnotation;
	}
}
