package uk.ac.ebi.quickgo.solr.mapper.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation.SolrAnnotationDocumentType;

/**
 * To convert Solr annotations into "QuickGO" Annotations
 * @author cbonill
 */
public class EntityAnnotationMapper implements EntityMapper<SolrAnnotation, Annotation>{

	@Override
	public Annotation toEntityObject(Collection<SolrAnnotation> solrObjects) {
		return toEntityObject(solrObjects, SolrAnnotationDocumentType.getAsInterfaces());
	}

	@Override
	public Annotation toEntityObject(Collection<SolrAnnotation> solrObjects,
			List<SolrDocumentType> solrDocumentTypes) {

		Annotation annotation = new Annotation();

		for (SolrDocumentType annotationDocumentType : solrDocumentTypes) {
			SolrAnnotationDocumentType solrAnnotationDocumentType = ((SolrAnnotationDocumentType) annotationDocumentType);

			switch (solrAnnotationDocumentType) {
			case ANNOTATION:
				if(getAssociatedSolrTerms(solrObjects,SolrAnnotationDocumentType.ANNOTATION).size() > 0){
					mapAnnotation(getAssociatedSolrTerms(solrObjects,
								SolrAnnotationDocumentType.ANNOTATION).get(0), annotation);
				}
				break;
			}
		}
		return annotation;
	}


	/**
	 * Maps a Solr annotation to an Annotation
	 * @param associatedSolrTerm Solr annotations
	 * @param annotation Annotation
	 */
	private void mapAnnotation(SolrAnnotation associatedSolrTerm,
			Annotation annotation) {
		annotation.setAssignedBy(associatedSolrTerm.getAssignedBy());
		annotation.setDate(associatedSolrTerm.getDate());
		annotation.setDb(associatedSolrTerm.getDb());
		annotation.setDbObjectID(associatedSolrTerm.getDbObjectID());
		annotation.setDbObjectName(associatedSolrTerm.getDbObjectName());
		annotation.setDbObjectSymbol(associatedSolrTerm.getDbObjectSymbol());
		annotation.setDbObjectType(associatedSolrTerm.getDbObjectType());
		annotation.setDbObjectSynonyms(associatedSolrTerm.getDbObjectSynonyms());
		annotation.setEcoID(associatedSolrTerm.getEcoID());
		annotation.setTermName(associatedSolrTerm.getTermName());
		annotation.setExtensions(associatedSolrTerm.getExtensions());
		annotation.setFullExtension(associatedSolrTerm.getFullExtension());
		annotation.setGoAspect(associatedSolrTerm.getGoAspect());
		annotation.setGoEvidence(associatedSolrTerm.getGoEvidence());
		annotation.setGoID(associatedSolrTerm.getGoID());
		annotation.setInteractingTaxID(associatedSolrTerm.getInteractingTaxID());
		annotation.setProperties(associatedSolrTerm.getProperties());
		annotation.setQualifier(associatedSolrTerm.getQualifier());
		annotation.setReference(associatedSolrTerm.getDbXref());
		annotation.setTaxonomyId(associatedSolrTerm.getTaxonomyId());
		annotation.setTaxonomyName(associatedSolrTerm.getTermName());
		annotation.setWith(associatedSolrTerm.getWith());
		annotation.setFullWith(associatedSolrTerm.getFullWith());
		annotation.setSequenceLength(associatedSolrTerm.getSequenceLength());
		annotation.setGp2protein(associatedSolrTerm.getGp2proteinList());
		annotation.setSubset(associatedSolrTerm.getSubSets());
		annotation.setAncestorsIPO(associatedSolrTerm.getAncestorsIPO());
		annotation.setEcoAncestorsI(associatedSolrTerm.getEcoAncestorsI());
	}

	/**
	 * Given a list of Solr annotations, returns the ones that match with the
	 * specified document type
	 *
	 * @param solrObjects
	 *            Solr annotations objects
	 * @param solrAnnotationDocumentType
	 *            Type to check
	 * @return Solr annotations that match with the specified document type
	 */
	protected List<SolrAnnotation> getAssociatedSolrTerms(Collection<SolrAnnotation> solrObjects,
			SolrAnnotationDocumentType solrAnnotationDocumentType) {
		List<SolrAnnotation> solrAnnotations = new ArrayList<>();
		for (SolrAnnotation solrAnnotation : solrObjects) {
			if (SolrAnnotationDocumentType.valueOf(solrAnnotation.getDocType().toUpperCase()) == solrAnnotationDocumentType) {
				solrAnnotations.add(solrAnnotation);
			}
		}
		return solrAnnotations;
	}
}
