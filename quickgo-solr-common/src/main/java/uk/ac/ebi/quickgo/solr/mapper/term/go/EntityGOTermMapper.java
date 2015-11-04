package uk.ac.ebi.quickgo.solr.mapper.term.go;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EntityGOTermMapper extends EntityTermMapper<GOTerm> {
    private static final Logger logger = LoggerFactory.getLogger(EntityGOTermMapper.class);

    @Override
    public void mapSpecificFields(GOTerm term,
            Collection<SolrTerm> solrObjects,
            List<SolrDocumentType> solrDocumentTypes) {

        for (SolrDocumentType termDocumentType : solrDocumentTypes) {
            switch ((SolrTermDocumentType) termDocumentType) {
                case TERM:
                    getTermDocument(solrObjects).ifPresent(doc -> mapAspectAndUsage(doc, term));
                case CONSTRAINT:
                    mapConstraints(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.CONSTRAINT), term);
                    break;
                case GUIDELINE:
                    mapAnnotationGuideline(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.GUIDELINE), term);
                    break;
                case ONTOLOGYRELATION:
                    mapOntologyRelations(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.ONTOLOGYRELATION),
                            term);
                    break;
            }
        }
    }

    @Override protected GOTerm createEmptyTerm() {
        return new GOTerm();
    }

    private void mapAspectAndUsage(SolrTerm solrTerm, GOTerm term) {
        try {
            if (solrTerm.getOntology() != null) {
                term.setAspect(GOTerm.EGOAspect.fromString(solrTerm.getOntology()));
            }

            if (solrTerm.getUsage() != null) {
                term.setUsage(GOTerm.ETermUsage.fromString(solrTerm.getUsage()));
            }
        } catch (Exception e) {
            logger.error("Error occurred whilst converting document to entity: {}", e);
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
        for (SolrTerm tc : associatedSolrTerms) {
            List<String> pubMedIds = tc.getPubMedIds();
            String sources = (pubMedIds != null) ? StringUtils.arrayToCommaDelimitedString(pubMedIds.toArray()) : "";

            taxonConstraints.add(new TaxonConstraint(tc.getTaxonConstraintRuleId(), tc.getTaxonConstraintAncestorId(),
                    tc.getTaxonConstraintName(), tc.getTaxonConstraintRelationship(), tc.getTaxonConstraintTaxIdType(),
                    tc.getTaxonConstraintTaxId(), tc.getTaxonConstraintTaxName(), sources));
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
    private void mapAnnotationGuideline(List<SolrTerm> associatedSolrTerms, GOTerm term) {
        List<NamedURL> annotationGuidelines = new ArrayList<>();
        for (SolrTerm guideline : associatedSolrTerms) {
            annotationGuidelines
                    .add(new NamedURL(guideline.getAnnotationGuidelineTitle(), guideline.getAnnotationGuidelineUrl()));
        }
        term.setGuidelines(annotationGuidelines);
    }

    /**
     * Map cross ontology relations information
     * @param associatedSolrTerms Terms to map
     * @param term Term populated with cross ontology relations information
     */
    private void mapOntologyRelations(List<SolrTerm> associatedSolrTerms, GOTerm term) {
        if (associatedSolrTerms != null) {
            List<CrossOntologyRelation> crossOntologyRelations = new ArrayList<>();
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