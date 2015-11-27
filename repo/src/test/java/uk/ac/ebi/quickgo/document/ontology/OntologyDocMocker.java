package uk.ac.ebi.quickgo.document.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.document.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Class to create mocked objects of different {@code docType}s, which are valid according to {@link OntologyDocument}.
 *
 * Created 03/11/15
 * @author Edd
 */
public class OntologyDocMocker {

    public static OntologyDocument createGODoc(String id, String name) {
        OntologyDocument od = createOBODoc(id, name);
        od.ontologyType = OntologyType.GO.name();
        od.usage = "some usage";
        od.aspect = Arrays.asList("aspect1", "aspect2");

        return od;
    }

    public static OntologyDocument createECODoc(String id, String name) {
        OntologyDocument od = createOBODoc(id, name);
        od.ontologyType = OntologyType.ECO.name();

        return od;
    }

    public static OntologyDocument createOBODoc(String id, String name) {
        OntologyDocument od = new OntologyDocument();
        od.id = id;
        od.name = name;
        od.definition = "The chemical reactions and pathways involving creatine (N-(aminoiminomethyl)" +
                "-N-methylglycine), a compound synthesized from the amino acids arginine, glycine, and methionine " +
                "that occurs in muscle.";
        od.subsets = Arrays.asList("goslim_pombe",
                "goslim_generic",
                "goslim_yeast",
                "goslim_chembl");
        od.isObsolete = true;
        od.replacedBy = "GO:0000002";
        od.considers = Arrays.asList("GO:0000003", "GO:0000004");
        od.comment = "Note that protein targeting encompasses the transport of the protein to " +
                "the specified location, and may also include additional steps such as protein processing.";
        od.children = Arrays.asList("GO:0000011", "GO:0000012");
        od.synonymNames = Arrays.asList("creatine anabolism", "crayola testarossa");
        od.secondaryIds = Arrays.asList("GO:0000003", "GO:0000004");

        // ------ nested, stored fields, which require reconstructing -------
        // example synonyms
        od.synonyms = new ArrayList<>();
        od.synonyms.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("creatine anabolism"))
                        .addField(newFlatFieldLeaf("exact"))
                        .buildString()
        );
        od.synonyms.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("crayola testarossa"))
                        .addField(newFlatFieldLeaf("inprecise"))
                        .buildString()
        );

        // example history
        od.history = new ArrayList<>();
        od.history.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("Gonna do something like it's ... "))
                        .addField(newFlatFieldLeaf("11:59, 31 Dec, 1999"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Textual description"))
                        .buildString()
        );
        od.history.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("History name"))
                        .addField(newFlatFieldLeaf("Tuesday next week"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Okay"))
                        .buildString()
        );

        // example xrefs
        od.xrefs = new ArrayList<>();
        od.xrefs.add(newFlatField()
                .addField(newFlatFieldLeaf("InterPro"))
                .addField(newFlatFieldLeaf("IPR031034"))
                .addField(newFlatFieldLeaf("Creatinine amidohydrolase"))
                .buildString());
        od.xrefs.add(newFlatField()
                .addField(newFlatFieldLeaf("AnotherXref"))
                .addField(newFlatFieldLeaf("IPR031035"))
                .addField(newFlatFieldLeaf("Pickled Onions"))
                .buildString());

        // example taxonomy constraints
        // format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2|blacklist
        od.taxonConstraints = new ArrayList<>();
        od.taxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0005623"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131567"))
                .addField(newFlatFieldLeaf("NCBITaxon"))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("PMID:00000001"))
                        .addField(newFlatFieldLeaf("PMID:00000002")))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("blacklist item 0"))
                        .addField(newFlatFieldLeaf("blacklist item 1")))
                .buildString());
        od.taxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0005624"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131568"))
                .addField(newFlatFieldLeaf("NCBITaxon"))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("PMID:00000003"))
                        .addField(newFlatFieldLeaf("PMID:00000004")))
                .buildString());

        // example xontology relations
        // format: xId|xTerm|xNamespace|xUrl|xRelation
        od.xRelations = new ArrayList<>();
        od.xRelations.add(newFlatField()
                .addField(newFlatFieldLeaf("CHEBI:16919"))
                .addField(newFlatFieldLeaf("creatine"))
                .addField(newFlatFieldLeaf("CHEBI"))
                .addField(newFlatFieldLeaf("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919"))
                .addField(newFlatFieldLeaf("has_participant"))
                .buildString());
        od.xRelations.add(newFlatField()
                .addField(newFlatFieldLeaf("CHEBI:16920"))
                .addField(newFlatFieldLeaf("creatiney"))
                .addField(newFlatFieldLeaf("CHEBI"))
                .addField(newFlatFieldLeaf("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16920"))
                .addField(newFlatFieldLeaf("has_participant"))
                .buildString());

        return od;
    }

    public static class Term extends OntologyDocument {

        public static OntologyDocument createGOTerm() {
            OntologyDocument term = new OntologyDocument();
            term.docType = OntologyDocument.Type.TERM.getValue();
            term.id = "0006600";
            term.ontologyType = "go";
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
            term.ontologyType = "eco";
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

    //    public static class Synonym extends OntologyDocument {
    //
    //        public static OntologyDocument createSynonym() {
    //            OntologyDocument synonym = new OntologyDocument();
    //            synonym.docType = OntologyDocument.Type.SYNONYM.getValue();
    //            synonym.synonymName = "creatine anabolism";
    //            synonym.synonymType = "exact";
    //            return synonym;
    //        }
    //    }

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
