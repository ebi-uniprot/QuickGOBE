/**
 * 
 */
package uk.ac.ebi.quickgo.ff.load;

import uk.ac.ebi.quickgo.ff.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.ontology.GOSourceFiles.*;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;

/**
 * Class that represents the Gene Ontology (GO)
 * 
 * @author tonys
 *
 */
public class GeneOntology extends GenericOntology {
    private static final String root = "GO:0003673";

    public GeneOntology() {
    	super("GO");
    }
    
    public void load(GOSourceFiles sourceFiles) throws Exception {
        for (String[] row : sourceFiles.goTerms.reader(EGOTerm.GO_ID, EGOTerm.NAME, EGOTerm.CATEGORY, EGOTerm.IS_OBSOLETE)) {
            addTerm(new GOTerm(row[0], row[1], row[2], row[3]));
        }

        super.load(sourceFiles, root);
        
	    for (String[] row : sourceFiles.proteinComplexes.reader(EProteinComplex.GO_ID, EProteinComplex.DB, EProteinComplex.DB_OBJECT_ID, EProteinComplex.DB_OBJECT_SYMBOL, EProteinComplex.DB_OBJECT_NAME)) {
		    GOTerm term = (GOTerm)getTerm(row[0]);
		    if (term != null) {
			    term.associateProteinComplex(row[1], row[2], row[3], row[4]);				
		    }
	    }

	    for (String[] row : sourceFiles.taxonUnions.reader(ETaxonUnion.UNION_ID, ETaxonUnion.NAME, ETaxonUnion.TAXA)) {
		    taxonConstraints.addTaxonUnion(row[0], row[1], row[2]);
	    }

	    for (String[] row : sourceFiles.taxonConstraints.reader(ETaxonConstraint.RULE_ID, ETaxonConstraint.GO_ID, ETaxonConstraint.NAME, ETaxonConstraint.RELATIONSHIP, ETaxonConstraint.TAX_ID_TYPE, ETaxonConstraint.TAX_ID, ETaxonConstraint.TAXON_NAME, ETaxonConstraint.SOURCES)) {
		    taxonConstraints.addConstraint(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7]);
	    }

	    for (String[] row : sourceFiles.termTaxonConstraints.reader(ETermTaxonConstraint.GO_ID, ETermTaxonConstraint.RULE_ID)) {
		    GOTerm term = (GOTerm)getTerm(row[0]);
		    if (term != null) {
			    term.addTaxonConstraint(taxonConstraints.get(row[1]));
		    }
	    }

	    for (String[] row : sourceFiles.annotationGuidelines.reader(EAnnotationGuidelineInfo.GO_ID, EAnnotationGuidelineInfo.TITLE, EAnnotationGuidelineInfo.URL)) {
		    GOTerm term = (GOTerm)getTerm(row[0]);
		    if (term != null) {
			    term.addGuideline(row[1], row[2]);
		    }
	    }

	    for (String[] row : sourceFiles.plannedGOChanges.reader(EPlannedGOChangeInfo.GO_ID, EPlannedGOChangeInfo.TITLE, EPlannedGOChangeInfo.URL)) {
		    GOTerm term = (GOTerm)getTerm(row[0]);
		    if (term != null) {
			    term.addPlannedChange(row[1], row[2]);
		    }
	    }
    }
}
