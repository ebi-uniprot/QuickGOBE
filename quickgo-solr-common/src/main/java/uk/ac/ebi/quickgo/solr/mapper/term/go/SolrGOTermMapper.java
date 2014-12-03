package uk.ac.ebi.quickgo.solr.mapper.term.go;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.solr.mapper.term.SolrTermMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;

/**
 * Mapper for GO terms
 */
@Service("solrGOTermMapper")
public class SolrGOTermMapper extends SolrTermMapper{

	@Override
	public void mapSpecificFields(GenericTerm term, List<SolrTerm> solrTerms, List<SolrDocumentType> solrDocumentTypes) {
		
		GOTerm goTerm = (GOTerm)term;
		
		for (SolrDocumentType termDocumentType : solrDocumentTypes) {
			SolrTermDocumentType solrTermDocumentType = ((SolrTermDocumentType) termDocumentType);

			switch (solrTermDocumentType) {
		
			case CONSTRAINT:
				solrTerms.addAll(mapTaxonConstraint(goTerm));
				break;
			case GUIDELINE:
				solrTerms.addAll(mapAnnotationGuideline(goTerm));
				break;
			}
		}
		
	}


    /**
     * Map SolR documents for taxonomy constraints of a GO term
     * @param term GO Term
     * @return SolR documents to be indexed
     */
	private Collection<SolrTerm> mapTaxonConstraint(GOTerm term) {
		Collection<SolrTerm> solrTermTaxonConstaints = new ArrayList<SolrTerm>();
		
		for (TaxonConstraint goTaxonConstraint : term.taxonConstraints) {			
			SolrTerm solrTermTaxonConstraint = new SolrTerm();
			solrTermTaxonConstraint.setDocType(SolrTermDocumentType.CONSTRAINT.getValue());
			solrTermTaxonConstraint.setId(term.getId());			
			solrTermTaxonConstraint.setTaxonConstraintRuleId(goTaxonConstraint.getRuleId());
			solrTermTaxonConstraint.setTaxonConstraintAncestorId(goTaxonConstraint.getGoId());			
			solrTermTaxonConstraint.setTaxonConstraintName(goTaxonConstraint.getName());
			solrTermTaxonConstraint.setTaxonConstraintRelationship(goTaxonConstraint.relationship());
			solrTermTaxonConstraint.setTaxonConstraintTaxIdType(goTaxonConstraint.taxIdType());
			solrTermTaxonConstraint.setTaxonConstraintTaxId(goTaxonConstraint.getTaxId());
			solrTermTaxonConstraint.setTaxonConstraintTaxName(goTaxonConstraint.getTaxonName());
			solrTermTaxonConstraint.setPubMedIds(goTaxonConstraint.getSourcesIds());
			
			solrTermTaxonConstaints.add(solrTermTaxonConstraint);
		}
		return solrTermTaxonConstaints;
	}
	
	/**
     * Map SolR terms for annotation guidelines of a GO term
     * @param term GO Term
     * @return SolR terms to be indexed
     */
	private Collection<SolrTerm> mapAnnotationGuideline(GOTerm term) {
		Collection<SolrTerm> solrTermGuidelines = new ArrayList<SolrTerm>();
		for (GOTerm.NamedURL goGuideline : term.guidelines) {
			SolrTerm solrTermGuideline = new SolrTerm();
			solrTermGuideline.setDocType(SolrTermDocumentType.GUIDELINE.getValue());
			solrTermGuideline.setId(term.getId());
			solrTermGuideline.setAnnotationGuidelineTitle(goGuideline.title);
			solrTermGuideline.setAnnotationGuidelineUrl(goGuideline.url);
			solrTermGuidelines.add(solrTermGuideline);
		}
		return solrTermGuidelines;
	}
}