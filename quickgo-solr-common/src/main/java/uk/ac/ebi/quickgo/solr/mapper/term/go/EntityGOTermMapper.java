package uk.ac.ebi.quickgo.solr.mapper.term.go;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.ontology.generic.CrossOntologyRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.NamedURL;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.solr.mapper.term.EntityTermMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;

/**
 * For creating GO Terms from Solr ones
 */
@Service("entityGOTermMapper")
public class EntityGOTermMapper extends EntityTermMapper {

	@Override
	public void mapSpecificFields(GOTerm term,
			Collection<SolrTerm> solrObjects,
			List<SolrDocumentType> solrDocumentTypes) {

		for (SolrDocumentType termDocumentType : solrDocumentTypes) {
			SolrTermDocumentType solrTermDocumentType = ((SolrTermDocumentType) termDocumentType);

			switch (solrTermDocumentType) {

			case CONSTRAINT:
				mapConstraints(
						getAssociatedSolrTerms(solrObjects,
								SolrTermDocumentType.CONSTRAINT), term);
				break;
			case GUIDELINE:
				mapAnnotationGuideline(
						getAssociatedSolrTerms(solrObjects,
								SolrTermDocumentType.GUIDELINE), term);
				break;
			case ONTOLOGYRELATION:
				mapOntologyRelations(getAssociatedSolrTerms(solrObjects,
						SolrTermDocumentType.ONTOLOGYRELATION), term);
			}
		}
	}	

	/**
	 * Map Taxon Constraints information
	 * 
	 * @param associatedSolrTerms
	 * @param term
	 */
	private void mapConstraints(List<SolrTerm> associatedSolrTerms, GOTerm term) {
		List<TaxonConstraint> taxonConstraints = new ArrayList<>();
		for (SolrTerm taxonConstraintsolrTerm : associatedSolrTerms) {
			List<String> pubMedIds = taxonConstraintsolrTerm.getPubMedIds();
			String sources = "";
			if (pubMedIds != null) {
				sources = StringUtils.arrayToCommaDelimitedString(pubMedIds.toArray());
			}
			
			TaxonConstraint taxonConstraint = new TaxonConstraint(
					taxonConstraintsolrTerm.getTaxonConstraintRuleId(),
					taxonConstraintsolrTerm.getTaxonConstraintAncestorId(),
					taxonConstraintsolrTerm.getTaxonConstraintName(),
					taxonConstraintsolrTerm.getTaxonConstraintRelationship(),
					taxonConstraintsolrTerm.getTaxonConstraintTaxIdType(),
					taxonConstraintsolrTerm.getTaxonConstraintTaxId(),
					taxonConstraintsolrTerm.getTaxonConstraintTaxName(),
					sources);
			taxonConstraints.add(taxonConstraint);
		}
		term.setTaxonConstraints(taxonConstraints);
	}

	/**
	 * Map Annotation Guidelines information
	 * 
	 * @param associatedSolrTerms
	 *            Annotation Guidelines Solr objects
	 * @param term
	 *            Term with the Annotation Guidelines information mapped
	 */
	private void mapAnnotationGuideline(List<SolrTerm> associatedSolrTerms,
			GOTerm term) {
		List<NamedURL> annotationGuidelines = new ArrayList<>();
		for (SolrTerm guideline : associatedSolrTerms) {
			NamedURL namedURL = new NamedURL(
					guideline.getAnnotationGuidelineTitle(),
					guideline.getAnnotationGuidelineUrl());
			annotationGuidelines.add(namedURL);
		}
		term.setGuidelines(annotationGuidelines);
	}
	
	/**
	 * Map cross ontology relations information
	 * @param associatedSolrTerms Terms to map
	 * @param term Term populated with cross ontology relations information 
	 */
	private void mapOntologyRelations(List<SolrTerm> associatedSolrTerms,
			GOTerm term) {
		List<CrossOntologyRelation> crossOntologyRelations = new ArrayList<>();
		if(associatedSolrTerms != null){
			for (SolrTerm ontologyRelation : associatedSolrTerms) {
				CrossOntologyRelation crossOntologyRelation = new CrossOntologyRelation(
						ontologyRelation.getCrossOntologyRelation(),
						ontologyRelation.getCrossOntologyOtherNamespace(),
						ontologyRelation.getCrossOntologyForeignId(),
						ontologyRelation.getCrossOntologyForeignTerm(), ontologyRelation.getCrossOntologyUrl());
				crossOntologyRelations.add(crossOntologyRelation);				
			}
			term.setCrossOntologyRelations(crossOntologyRelations);
		}
	}
}