package uk.ac.ebi.quickgo.search.ontology;

import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

import java.util.Arrays;
import java.util.Date;

/**
 * Class to create mocked objects of different {@code docType}s, which are valid according to {@link SolrTerm}.
 *
 * Created 03/11/15
 * @author Edd
 */
public class DocumentMocker {

    public static class Relation extends SolrTerm {
        public static SolrTerm createRelation() {
            SolrTerm relation = new SolrTerm();
            relation.setDocType("relation");
            relation.setObsolete(false);
            relation.setChild("GO:0006601");
            relation.setParent("GO:0006601");
            relation.setRelationType("I");
            return relation;
        }
    }

    protected static class Term extends SolrTerm {

        protected static SolrTerm createTerm() {
            SolrTerm term = new SolrTerm();
            term.setDocType("term");
            term.setId("GO:0006600");
            term.setName("creatine metabolic process");
            term.setComment("Note that protein targeting encompasses the transport of the protein to " +
                    "the specified location, and may also include additional steps such as protein processing.");
            term.setObsolete(false);
            //        document.setCategory(CATEGORY); // not used
            term.setDefinition(
                    "The chemical reactions and pathways involving creatine (N-(aminoiminomethyl)-N-methylglycine), a "
                            + "compound synthesized from the amino acids arginine, glycine, and methionine that occurs in "
                            + "muscle.");
            term.setDefinitionXref(Arrays.asList("PMID:00000001", "PMID:00000002"));
            term.setSecondaryIds(Arrays.asList("GO:0000003", "GO:0000004"));
            return term;
        }
    }

    public static class Synonym extends SolrTerm {

        public static SolrTerm createSynonym() {
            SolrTerm synonym = new SolrTerm();
            synonym.setDocType("synonym");
            synonym.setSynonymName("creatine anabolism");
            synonym.setSynonymType("exact");
            return synonym;
        }
    }

    public static class TaxonConstraint extends SolrTerm {

        public static SolrTerm createTaxonConstraint() {
            SolrTerm tc =new SolrTerm();
            tc.setDocType(
                    "constraint");
            tc.setTaxonConstraintRuleId("GOTAX:0000057");
            tc.setTaxonConstraintRelationship("only_in_taxon");
            tc.setTaxonConstraintAncestorId("GO:0005623");
            tc.setTaxonConstraintTaxId("131567");
            tc.setTaxonConstraintTaxIdType("NCBITaxon");
            tc.setTaxonConstraintName("cell");
            tc.setTaxonConstraintTaxName("cellular organisms");
            tc.setPubMedIds(Arrays.asList("PMID:00000002", "PMID:00000001"));
            return tc;
        }
    }

    public static class XRef extends SolrTerm {
        public static SolrTerm createXref() {
            SolrTerm xref = new SolrTerm();
            xref.setDocType("xref");
            xref.setXrefDbCode("InterPro");
            xref.setXrefDbId("IPR031034");
            xref.setXrefName("Creatinine amidohydrolase");
            return xref;
        }
    }

    public static class Replaces extends SolrTerm {
        public static SolrTerm createReplaces() {
            SolrTerm replaces = new SolrTerm();
            replaces.setDocType("replace");
            replaces.setObsoleteId("GO:0003929");
            replaces.setReason("Consider");
            return replaces;
        }
    }

    public static class AnnotationGuideLines extends SolrTerm {
        public static SolrTerm createAnnotationGuideLines() {
            SolrTerm agl = new SolrTerm();
            agl.setDocType("ontologyrelation");
            agl.setCrossOntologyRelation("has_participant");
            agl.setCrossOntologyForeignTerm("creatine");
            agl.setCrossOntologyForeignId("CHEBI:16919");
            agl.setCrossOntologyOtherNamespace("CHEBI");
            agl.setCrossOntologyUrl("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919");
            return agl;
        }
    }

    public static class ChangeLog extends SolrTerm {
        public static SolrTerm createChangeLog() {
            SolrTerm cl = new SolrTerm();
            cl.setDocType("history");
            cl.setHistoryAction("A");
            cl.setHistoryCategory("RELATION");
            cl.setHistoryName("creatine metabolic process");
            cl.setHistoryText("is a GO:1901605 (alpha-amino acid metabolic process)");
            cl.setHistoryTimeStamp(new Date());
            return cl;
        }
    }

    public static class OBOTerms extends SolrTerm {
        public static SolrTerm createOBOTerms() {
            SolrTerm obo = new SolrTerm();
            obo.setDocType("term");
            obo.setSubsets(Arrays.asList("goslim_pombe",
                    "goslim_generic",
                    "goslim_yeast",
                    "goslim_chembl"));
            return obo;
        }
    }
}
