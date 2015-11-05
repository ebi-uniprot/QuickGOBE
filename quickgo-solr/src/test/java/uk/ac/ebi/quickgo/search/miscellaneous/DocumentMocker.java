package uk.ac.ebi.quickgo.search.miscellaneous;

import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;

import java.util.Arrays;
import java.util.Collections;

/**
 * Class to create mocked objects of different {@code docType}s, which are valid according to {@link SolrMiscellaneous}.
 *
 * Used in {@link MiscellaneousSearchIT} to add documents to the ontology index, so that it is possible to test
 * the behaviour of searching the index (i.e., testing the effect of the index's schema's field definitions).
 *
 * Created 03/11/15
 * @author Edd
 */
public class DocumentMocker {

    private static final String PLACEHOLDER_TEXT = "placeholder: please add valid data for this field";

    public static class PreCalculatedStats extends SolrMiscellaneous {
        public static SolrMiscellaneous createPreCalculatedStats() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.PRECALCULATED_STATS.getValue());
            doc.setStatisticTupleType(PLACEHOLDER_TEXT);
            doc.setStatisticTupleKey(PLACEHOLDER_TEXT);
            doc.setStatisticTupleHits(100L);
            return doc;
        }
    }

    public static class PostProcessingRule extends SolrMiscellaneous {
        public static SolrMiscellaneous createPostProcessingRule() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.POSTPROCESSINGRULE.getValue());
            doc.setPprRuleId("GOTAX:0000057");
            doc.setPprAncestorGoId("GO:0005623");
            doc.setPprAncestorTerm("cell");
            doc.setPprRelationship("only_in_taxon");
            doc.setPprTaxonName("cellular organisms");
            doc.setPprOriginalGoId("GO:0005618");
            doc.setPprOriginalTerm("cell wall");
            doc.setPprCleanupAction("TRANSFORM");
            doc.setPprAffectedTaxGroup("Viruses");
            doc.setPprSubstitutedGoId("GO:0044158");
            doc.setPprSubstitutedTerm("");
            doc.setPprCuratorNotes("Proteins are viral,located in a cell wall,viruses have none so 'host'");
            return doc;
        }
    }

    public static class Evidence extends SolrMiscellaneous {
        public static SolrMiscellaneous createEvidence() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.EVIDENCE.getValue());
            doc.setEvidenceCode("EXP");
            doc.setEvidenceName("Inferred from EXPeriment");
            return doc;
        }
    }

    public static class SubSetCount extends SolrMiscellaneous {
        public static SolrMiscellaneous createSubSetCount() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.SUBSETCOUNT.getValue());
            doc.setSubset("goslim_chembl");
            doc.setSubsetCount(309);
            return doc;
        }
    }

    public static class XRefDB extends SolrMiscellaneous {
        public static SolrMiscellaneous createXRefDB() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.XREFDB.getValue());
            doc.setXrefAbbreviation("JCVI_CMR");
            doc.setXrefDatabase("EGAD database at the J. Craig Venter Institute");
            doc.setXrefGenericURL("http://cmr.jcvi.org/");
            //            doc.setXrefUrlSyntax(""); // no example values found
            return doc;
        }
    }

    public static class Extension extends SolrMiscellaneous {
        public static SolrMiscellaneous createExtension() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.EXTENSION.getValue());
            doc.setAerName("activated_by");
            doc.setAerUsage("Identifies a chemical substance that increases the activity of the gene product.");
            doc.setAerDomain("GO:0003674");

            doc.setAerParents(Collections.singletonList("_ROOT_AER_"));
            doc.setAerRange("(^CHEBI:([0-9]{1,6})$)|(^ChEBI:([0-9]{1,6})$)|(^KEGG_LIGAND:(C\\\\d{5})$)|" +
                    "(^PubChem_Compound:([0-9]+)$)|(^PubChem_Substance:([0-9]{4,})$)");
            //            doc.setAerSecondaries(); // no example values found
            doc.setAerSubsets(Arrays.asList("all_relations", "chemical"));
            return doc;
        }
    }

    public static class Taxonomy extends SolrMiscellaneous {
        public static SolrMiscellaneous createTaxonomy() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.TAXONOMY.getValue());
            doc.setTaxonomyClosures(
                    Arrays.asList(194321,
                            1177,
                            1162,
                            1161,
                            1117,
                            2,
                            131567));
            doc.setTaxonomyId(194321);
            doc.setTaxonomyName("Nostoc sp. 'Nephroma resupinatum cyanobiont 40'");

            return doc;
        }
    }

    public static class Stats extends SolrMiscellaneous {
        public static SolrMiscellaneous createStats() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.STATS.getValue());
            doc.setTerm("0005515");
            doc.setComparedTerm("0005515");
            doc.setTogether(36079);
            doc.setCompared(36014);
            doc.setSelected(36079);
            doc.setAll(407335);
            doc.setStatsType("nonIEA");
            return doc;
        }
    }

    public static class Sequence extends SolrMiscellaneous {
        public static SolrMiscellaneous createSequence() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.SEQUENCE.getValue());
            doc.setDbObjectID("A0A000");
            doc.setSequence("MDFFVRLARETGDRKREFLELGRKAGRFPAASTSNGEISIWCSNDYLGMGQHPDVLDAMKRSVDEYGGGSGGSRNTGGTNHFHVALEREPAEPHGKEDAVLFTSGYSANEGSLSVLAGAVDDCQVFSDSANHASIIDGLRHSGARKHVFRHKDGRHLEELLAAADRDKPKFIALESVHSMRGDIALLAEIAGLAKRYGAVTFLDEVHAVGMYGPGGAGIAARDGVHCEFTVVMGTLAKAFGMTGGYVAGPAVLMDAVRARARSFVFTTALPPAVAAGALAAVRHLRGSDEERRRPAENARLTHGLLRERDIPVLSDRSPIVPVLVGEDRMCKRMSALPLERHGAYVQAIDAPSVPAGEEILRIAPSAVHETEEIHRFVDALDGIWSELGAARRV");
            return doc;
        }
    }

    public static class Publication extends SolrMiscellaneous {
        public static SolrMiscellaneous createPublication() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.PUBLICATION.getValue());
            doc.setPublicationID(11692459);
            doc.setPublicationTitle("The beat of hemodialysis: frequent, prolonged, or frequent and prolonged?");
            return doc;
        }
    }

    public static class GuideLine extends SolrMiscellaneous {
        public static SolrMiscellaneous createGuideLine() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.GUIDELINE.getValue());
            doc.setGuidelineTitle("Annotating to catalytic activities using the IPI evidence code");
            doc.setGuidelineURL("http://wiki.geneontology.org/index.php/Annotations_to_Catalytic_activity_with_IPI");
            return doc;
        }
    }

    public static class BlackList extends SolrMiscellaneous {
        public static SolrMiscellaneous createBlackList() {
            SolrMiscellaneous doc = new SolrMiscellaneous();
            doc.setDocType(SolrMiscellaneousDocumentType.BLACKLIST.getValue());
            doc.setDbObjectID("A0A023GQ97");
            doc.setBacklistReason("1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference: GO_REF:0000033");
//            doc.setBlacklistMethodID(""); // no example values found
            doc.setBacklistCategory("NOT-qualified manual");
            doc.setBacklistEntryType("dynamic");
            return doc;
        }
    }

}
