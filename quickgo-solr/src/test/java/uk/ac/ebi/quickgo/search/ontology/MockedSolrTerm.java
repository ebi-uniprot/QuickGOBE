package uk.ac.ebi.quickgo.search.ontology;

import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class to create mocked objects of different {@code docType}s, which are valid according to {@link SolrTerm}.
 *
 * Created 03/11/15
 * @author Edd
 */
public class MockedSolrTerm {

    private static final String ID = "GO:0006600";
    private static final List<String> SECONDARY_IDS = Arrays.asList("GO:0000003", "GO:0000004");
    private static final List<String> DEFINITION_XREF = Arrays.asList("PMID:00000001", "PMID:00000002");
    private static final String DEFINITION =
            "The chemical reactions and pathways involving creatine (N-(aminoiminomethyl)-N-methylglycine), a " +
                    "compound synthesized from the amino acids arginine, glycine, and methionine that occurs in " +
                    "muscle.";

    private static final boolean IS_OBSOLETE = false;
    private static final String COMMENT = "Note that protein targeting encompasses the transport of the protein to " +
            "the specified location, and may also include additional steps such as protein processing.";
    private static final String NAME = "creatine metabolic process";
    private static final String SYNONYM_NAME = "creatine anabolism";
    private static final String SYNONYM_TYPE = "exact";
    private static final String TAXON_CONSTRAINT_RULE_ID = "GOTAX:0000057";
    private static final List<String> PUBMED_IDS = Arrays.asList("PMID:21311032");
    private static final String RELATION_TYPE = "I";
    private static final String PARENT = "GO:0006601";
    private static final String CHILD = "GO:0006601";

    private static SolrTerm createBasic(String docType) {
        SolrTerm document = new SolrTerm();
        document.setDocType(docType);
        document.setId(ID);
        document.setName(NAME);
        document.setComment(COMMENT);
        //        document.setCategory(CATEGORY); // not used
        document.setObsolete(IS_OBSOLETE);
        document.setDefinition(DEFINITION);
        document.setDefinitionXref(DEFINITION_XREF);
        document.setSecondaryIds(SECONDARY_IDS);
        return document;
    }

    public static SolrTerm createRelation() {
        SolrTerm relation = createBasic("relation");
        relation.setChild(CHILD);
        relation.setParent(PARENT);
        relation.setRelationType(RELATION_TYPE);
        return relation;
    }

    public static SolrTerm createSynonym() {
        SolrTerm synonym = createBasic("synonym");
        synonym.setSynonymName(SYNONYM_NAME);
        synonym.setSynonymType(SYNONYM_TYPE);
        return synonym;
    }

    public static SolrTerm createTaxonConstraint() {
        SolrTerm tc = createBasic("constraint");
        tc.setTaxonConstraintRuleId(TAXON_CONSTRAINT_RULE_ID);
        tc.setTaxonConstraintRelationship("only_in_taxon");
        tc.setTaxonConstraintAncestorId("GO:0005623");
        tc.setTaxonConstraintTaxId("131567");
        tc.setTaxonConstraintTaxIdType("NCBITaxon");
        tc.setTaxonConstraintName("cell");
        tc.setTaxonConstraintTaxName("cellular organisms");
        tc.setPubMedIds(PUBMED_IDS);
        return tc;
    }

    public static SolrTerm createXref() {
        SolrTerm xref = createBasic("xref");
        xref.setXrefDbCode("InterPro");
        xref.setXrefDbId("IPR031034");
        xref.setXrefName("Creatinine amidohydrolase");
        return xref;
    }

    public static SolrTerm createReplaces() {
        SolrTerm replaces = createBasic("replace");
        replaces.setObsoleteId("GO:0003929");
        replaces.setReason("Consider");
        return replaces;
    }

    public static SolrTerm createAnnotationGuideLines() {
        SolrTerm agl = createBasic("ontologyrelation");
        agl.setCrossOntologyRelation("has_participant");
        agl.setCrossOntologyForeignTerm("creatine");
        agl.setCrossOntologyForeignId("CHEBI:16919");
        agl.setCrossOntologyOtherNamespace("CHEBI");
        agl.setCrossOntologyUrl("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919");
        return agl;
    }

    public static SolrTerm createChangeLog() {
        SolrTerm cl = createBasic("history");
        cl.setHistoryAction("A");
        cl.setHistoryCategory("RELATION");
        cl.setHistoryName("creatine metabolic process");
        cl.setHistoryText("is a GO:1901605 (alpha-amino acid metabolic process)");
        cl.setHistoryTimeStamp(new Date());
        return cl;
    }

    public static SolrTerm createOBOTerms() {
        SolrTerm obo = createBasic("term");
        obo.setSubsets(Arrays.asList("goslim_pombe",
                "goslim_generic",
                "goslim_yeast",
                "goslim_chembl"));
        return obo;
    }
}
