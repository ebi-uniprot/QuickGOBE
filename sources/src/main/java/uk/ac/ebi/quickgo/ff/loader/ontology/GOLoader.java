package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

/**
 * Loads a complete GO ontology from a suite of source files.
 *
 * Created by Edd on 11/12/2015.
 */
public class GOLoader extends AbstractGenericOLoader<GOSourceFiles, GeneOntology> {

    public GOLoader(GOSourceFiles sourceFiles) {
        super(sourceFiles);
    }

    @Override
    public GeneOntology load() throws Exception {
        GeneOntology go = getInstance();

        // first add all GO terms to the ontology (additional information will be added to each term later)
        for (String[] row : sourceFiles.goTerms.reader(GOSourceFiles.EGOTerm.GO_ID,
                GOSourceFiles.EGOTerm.NAME,
                GOSourceFiles.EGOTerm.CATEGORY,
                GOSourceFiles.EGOTerm.IS_OBSOLETE)) {
            go.addTerm(new GOTerm(row[0], row[1], row[2], row[3]));
        }

        // add generic ontology info
        createWithGenericOInfo(GeneOntology.NAME_SPACE, GeneOntology.root);

        for (String[] row : sourceFiles.taxonUnions.reader(GOSourceFiles.ETaxonUnion.UNION_ID,
                GOSourceFiles.ETaxonUnion.NAME,
                GOSourceFiles.ETaxonUnion.TAXA)) {
            go.taxonConstraints.addTaxonUnion(row[0], row[1], row[2]);
        }

        for (String[] row : sourceFiles.taxonConstraints.reader(GOSourceFiles.ETaxonConstraint.RULE_ID,
                GOSourceFiles.ETaxonConstraint.GO_ID,
                GOSourceFiles.ETaxonConstraint.NAME,
                GOSourceFiles.ETaxonConstraint.RELATIONSHIP,
                GOSourceFiles.ETaxonConstraint.TAX_ID_TYPE,
                GOSourceFiles.ETaxonConstraint.TAX_ID,
                GOSourceFiles.ETaxonConstraint.TAXON_NAME,
                GOSourceFiles.ETaxonConstraint.SOURCES)) {
            go.taxonConstraints.addConstraint(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7]);
        }

        for (String[] row : sourceFiles.termTaxonConstraints.reader(GOSourceFiles.ETermTaxonConstraint.GO_ID,
                GOSourceFiles.ETermTaxonConstraint.RULE_ID)) {
            GOTerm term = (GOTerm) go.getTerm(row[0]);
            if (term != null) {
                term.addTaxonConstraint(go.taxonConstraints.get(row[1]));
            }
        }

        for (String[] row : sourceFiles.annotationGuidelines.reader(GOSourceFiles.EAnnotationGuidelineInfo.GO_ID,
                GOSourceFiles.EAnnotationGuidelineInfo.TITLE,
                GOSourceFiles.EAnnotationGuidelineInfo.URL)) {
            GOTerm term = (GOTerm) go.getTerm(row[0]);
            if (term != null) {
                term.addGuideline(row[1], row[2]);
            }
        }

        for (String[] row : sourceFiles.plannedGOChanges.reader(GOSourceFiles.EPlannedGOChangeInfo.GO_ID,
                GOSourceFiles.EPlannedGOChangeInfo.TITLE,
                GOSourceFiles.EPlannedGOChangeInfo.URL)) {
            GOTerm term = (GOTerm) go.getTerm(row[0]);
            if (term != null) {
                term.addPlannedChange(row[1], row[2]);
            }
        }

        for (String[] row : sourceFiles.blacklistForGoTerm.reader(GOSourceFiles.EAnnBlacklistEntry.GO_ID,
                GOSourceFiles.EAnnBlacklistEntry.CATEGORY,
                GOSourceFiles.EAnnBlacklistEntry.ENTITY_TYPE,
                GOSourceFiles.EAnnBlacklistEntry.ENTITY_ID,
                GOSourceFiles.EAnnBlacklistEntry.TAXON_ID,
                GOSourceFiles.EAnnBlacklistEntry.ENTITY_NAME,
                GOSourceFiles.EAnnBlacklistEntry.ANCESTOR_GO_ID,
                GOSourceFiles.EAnnBlacklistEntry.REASON,
                GOSourceFiles.EAnnBlacklistEntry.METHOD_ID)) {
            GOTerm term = (GOTerm) go.getTerm(row[0]);
            if (term != null) {
                term.addBlacklist(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8]);
            }
        }

        return go;
    }

    @Override
    protected GeneOntology newInstance() {
        return new GeneOntology();
    }
}
