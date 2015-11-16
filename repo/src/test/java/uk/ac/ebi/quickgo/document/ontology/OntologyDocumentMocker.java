package uk.ac.ebi.quickgo.document.ontology;

import uk.ac.ebi.quickgo.repo.ontology.OntologyRepositoryTest;

import java.util.Arrays;
import java.util.Date;

/**
 * Class to create mocked objects of different {@code docType}s, which are valid according to {@link OntologyDocument}.
 *
 * Used in {@link OntologyRepositoryTest} to add documents to the ontology index, so that it is possible to test
 * the behaviour of searching the index (i.e., testing the effect of the index's schema's field definitions).
 *
 * Created 03/11/15
 * @author Edd
 */
public class OntologyDocumentMocker {

    public static OntologyDocument createSimpleOntologyDocument(String id, String name) {
        OntologyDocument ontologyDocument = new OntologyDocument();
        ontologyDocument.id = id;
        ontologyDocument.name = name;
        return ontologyDocument;
    }

    public static class Term extends OntologyDocument {

        public static OntologyDocument createGOTerm() {
            OntologyDocument term = new OntologyDocument();
            term.docType = OntologyDocument.Type.TERM.getValue();
            term.id = "0006600";
            term.idType = "go";
            term.name = "creatine metabolic process";
            term.comment = "Note that protein targeting encompasses the transport of the protein to " +
                    "the specified location, and may also include additional steps such as protein processing.";
            term.isObsolete = false;
            term.definition =
                    "The chemical reactions and pathways involving creatine (N-(aminoiminomethyl)-N-methylglycine), a "
                            +
                            "compound synthesized from the amino acids arginine, glycine, and methionine that occurs " +
                            "in "
                            + "muscle.";
            term.definitionXref = Arrays.asList("PMID:00000001", "PMID:00000002");
            term.secondaryIds = Arrays.asList("GO:0000003", "GO:0000004");
            return term;
        }

        public static OntologyDocument createECOTerm() {
            OntologyDocument term = new OntologyDocument();
            term.docType = OntologyDocument.Type.TERM.getValue();
            term.id = "0006600";
            term.idType = "eco";
            term.name = "creatine metabolic process";
            term.comment = "Note that protein targeting encompasses the transport of the protein to " +
                    "the specified location, and may also include additional steps such as protein processing.";
            term.isObsolete = false;
            term.definition =
                    "The chemical reactions and pathways involving creatine (N-(aminoiminomethyl)-N-methylglycine), a "
                            +
                            "compound synthesized from the amino acids arginine, glycine, and methionine that occurs " +
                            "in "
                            + "muscle.";
            term.definitionXref = Arrays.asList("PMID:00000001", "PMID:00000002");
            term.secondaryIds = Arrays.asList("GO:0000003", "GO:0000004");
            return term;
        }
    }

    public static class Synonym extends OntologyDocument {

        public static OntologyDocument createSynonym() {
            OntologyDocument synonym = new OntologyDocument();
            synonym.docType = OntologyDocument.Type.SYNONYM.getValue();
            synonym.synonymName = "creatine anabolism";
            synonym.synonymType = "exact";
            return synonym;
        }
    }

    public static class TaxonConstraint extends OntologyDocument {

        public static OntologyDocument createTaxonConstraint() {
            OntologyDocument tc = new OntologyDocument();
            tc.docType = "constraint";
            tc.taxonConstraintRuleId = "GOTAX:0000057";
            tc.taxonConstraintRelationship = "only_in_taxon";
            tc.taxonConstraintAncestorId = "GO:0005623";
            tc.taxonConstraintTaxId = "131567";
            tc.taxonConstraintTaxIdType = "NCBITaxon";
            tc.taxonConstraintName = "cell";
            tc.taxonConstraintTaxName = "cellular organisms";
            tc.pubMedIds = Arrays.asList("PMID:00000002", "PMID:00000001");
            return tc;
        }
    }

    public static class XRef extends OntologyDocument {
        public static OntologyDocument createXref() {
            OntologyDocument xref = new OntologyDocument();
            xref.docType = OntologyDocument.Type.XREF.getValue();
            xref.xrefDbCode = "InterPro";
            xref.xrefDbId = "IPR031034";
            xref.xrefName = "Creatinine amidohydrolase";
            return xref;
        }
    }

    public static class Replaces extends OntologyDocument {
        public static OntologyDocument createReplaces() {
            OntologyDocument replaces = new OntologyDocument();
            replaces.docType = OntologyDocument.Type.REPLACE.getValue();
            replaces.obsoleteId = "GO:0003929";
            replaces.reason = "Consider";
            return replaces;
        }
    }

    public static class AnnotationGuideLines extends OntologyDocument {
        public static OntologyDocument createAnnotationGuideLines() {
            OntologyDocument agl = new OntologyDocument();
            agl.docType = OntologyDocument.Type.ONTOLOGYRELATION.getValue();
            agl.crossOntologyRelation = "has_participant";
            agl.crossOntologyForeignTerm = "creatine";
            agl.crossOntologyForeignId = "CHEBI:16919";
            agl.crossOntologyOtherNamespace = "CHEBI";
            agl.crossOntologyUrl = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919";
            return agl;
        }
    }

    public static class ChangeLog extends OntologyDocument {
        public static OntologyDocument createChangeLog() {
            OntologyDocument cl = new OntologyDocument();
            cl.docType = OntologyDocument.Type.HISTORY.getValue();
            cl.historyAction = "A";
            cl.historyCategory = "RELATION";
            cl.historyName = "creatine metabolic process";
            cl.historyText = "is a GO:1901605 (alpha-amino acid metabolic process)";
            cl.historyTimeStamp = new Date();
            return cl;
        }
    }

    public static class OBOTerms extends OntologyDocument {
        public static OntologyDocument createOBOTerms() {
            OntologyDocument obo = new OntologyDocument();
            obo.docType = OntologyDocument.Type.TERM.getValue();
            obo.subsets = Arrays.asList("goslim_pombe",
                    "goslim_generic",
                    "goslim_yeast",
                    "goslim_chembl");
            return obo;
        }
    }
}
