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
    public static final String SYMBOL = "moeA5";
    public static final String OBJECT_TYPE = "protein";
    public static final int TAXON_ID = 12345;

    public static final List<String> TARGET_SET = asList("KRUK", "BHF-UCL", "Exosome");
    public static final String GP_SUBSET = "TrEMBL";
    public static final String GO_ASPECT = "cellular_component";
    public static final Date DATE = Date.from(
            LocalDate.of(1869, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());

    public static final String EXTENSION_DB1 = "UBERON";
    public static final String EXTENSION_DB2 = "CL";
    public static final String EXTENSION_DB3 = "UNIPROT";
    public static final String EXTENSION_ID1 = "0001675";
    public static final String EXTENSION_ID2 = "0000032";
    public static final String EXTENSION_ID3 = "0006000";
    public static final String EXTENSION_RELATIONSHIP1 = "results_in_development_of";
    public static final String EXTENSION_RELATIONSHIP2 = "acts_on_population_of";
    public static final String EXTENSION_RELATIONSHIP3 = "indicative_of";

    public static final String EXTENSION_1 = asExtension(EXTENSION_RELATIONSHIP1, EXTENSION_DB1, EXTENSION_ID1);
    public static final String EXTENSION_2 = asExtension(EXTENSION_RELATIONSHIP2 ,EXTENSION_DB2 ,EXTENSION_ID2);
    public static final String EXTENSION_3 = asExtension(EXTENSION_RELATIONSHIP3 ,EXTENSION_DB3,EXTENSION_ID3);
    public static final List<String> EXTENSIONS = asList(String.format("%s,%s", EXTENSION_1, EXTENSION_2), EXTENSION_3);

    private AnnotationDocMocker() {}

    public static AnnotationDocument createAnnotationDoc(String geneProductId) {
        AnnotationDocument doc = new AnnotationDocument();
        doc.geneProductId = geneProductId;

        // automatically compute a document identifier,
        // to overcome non-uniqueness of all other annotation fields
        // (in solrconfig.xml this is set automatically as a UUID)
        doc.rowNumber = System.nanoTime();
        doc.id = geneProductId + "!" + doc.rowNumber;

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

    public static String asExtension(String relationship, String db, String id) {
        return String.format("%s(%s:%s)",relationship, db, id);
    }
}
