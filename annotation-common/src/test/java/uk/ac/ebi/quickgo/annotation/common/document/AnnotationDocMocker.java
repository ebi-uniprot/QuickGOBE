package uk.ac.ebi.quickgo.annotation.common.document;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Class to create stubbed {@link AnnotationDocument} instances.
 *
 * Created 14/04/16
 * @author Edd
 */
public class AnnotationDocMocker {

    public static final String GO_ID = "GO:0003824";
    public static final String ECO_ID = "ECO:0000256";
    public static final String QUALIFIER = "enables";
    public static final String GO_EVIDENCE = "IEA";
    public static final String REFERENCE = "GO_REF:0000002";
    public static final List<String> WITH_FROM = asList("InterPro:IPR015421", "InterPro:IPR015422");
    public static final int INTERACTING_TAXON_ID = 35758;
    public static final String ASSIGNED_BY = "InterPro";
    public static final List<String> EXTENSIONS = asList(
            "results_in_development_of(UBERON:0001675),acts_on_population_of(CL:0000032)",
            "results_in_development_of(UBERON:0006000)");
    public static final String SYMBOL = "moeA5";
    public static final String OBJECT_TYPE = "protein";
    public static final int TAXON_ID = 12345;
    public static final List<String> TARGET_SET = asList("KRUK", "BHF-UCL", "Exosome");
    public static final String GP_SUBSET = "TrEMBL";
    public static final String GO_ASPECT = "cellular_component";
    public static final Date DATE = Date.from(
            LocalDate.of(1869, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());


    private AnnotationDocMocker() {}

    public static AnnotationDocument createAnnotationDoc(String geneProductId) {
        AnnotationDocument doc = new AnnotationDocument();
        doc.geneProductId = geneProductId;

        // automatically compute a document identifier,
        // to overcome non-uniqueness of all other annotation fields
        // (in solrconfig.xml this is set automatically as a UUID)
        doc.id = geneProductId + "-" + System.nanoTime();

        doc.goId = GO_ID;
        doc.evidenceCode = ECO_ID;
        doc.qualifier = QUALIFIER;
        doc.goEvidence = GO_EVIDENCE;
        doc.reference = REFERENCE;
        doc.withFrom = WITH_FROM;
        doc.interactingTaxonId = INTERACTING_TAXON_ID;
        doc.assignedBy = ASSIGNED_BY;
        doc.extensions = EXTENSIONS;
        doc.symbol = SYMBOL;
        doc.geneProductType = OBJECT_TYPE;
        doc.taxonId = TAXON_ID;
        doc.targetSets = TARGET_SET;
        doc.geneProductSubset = GP_SUBSET;
        doc.goAspect = GO_ASPECT;
        doc.date = DATE;

        return doc;
    }
}
