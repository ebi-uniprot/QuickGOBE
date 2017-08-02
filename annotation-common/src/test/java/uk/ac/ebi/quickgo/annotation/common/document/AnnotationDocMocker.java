package uk.ac.ebi.quickgo.annotation.common.document;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;

/**
 * Class to create stubbed {@link AnnotationDocument} instances.
 * <p>
 * Created 14/04/16
 *
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
    public static final List<Integer> TAXON_ANCESTORS = asList(12345, 1234, 123, 12, 1);

    public static final List<String> TARGET_SET = asList("KRUK", "BHF-UCL", "Exosome");
    public static final String GP_SUBSET = "TrEMBL";
    public static final String GO_ASPECT = "cellular_component";
    public static final Date DATE = Date.from(
            LocalDate.of(1869, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());

    public static final String EXTENSION_DB1 = "NCBI_gi";
    public static final String EXTENSION_DB2 = "Cl";
    public static final String EXTENSION_DB3 = "UBERON-AG";
    public static final String EXTENSION_DB4 = "PO1234";
    public static final String EXTENSION_ID1 = "0001675";
    public static final String EXTENSION_ID2 = "AbC:0032";
    public static final String EXTENSION_ID3 = "000-6000";
    public static final String EXTENSION_ID4 = "QWE_90hy";
    public static final String EXTENSION_RELATIONSHIP1 = "results_in_development_of";
    public static final String EXTENSION_RELATIONSHIP2 = "acts_on_population_of";
    public static final String EXTENSION_RELATIONSHIP3 = "indicative_of";
    public static final String EXTENSION_RELATIONSHIP4 = "happy_about";

    public static final String EXTENSION_1 = asExtension(EXTENSION_RELATIONSHIP1, EXTENSION_DB1, EXTENSION_ID1);
    public static final String EXTENSION_2 = asExtension(EXTENSION_RELATIONSHIP2, EXTENSION_DB2, EXTENSION_ID2);
    public static final String EXTENSION_3 = asExtension(EXTENSION_RELATIONSHIP3, EXTENSION_DB3, EXTENSION_ID3);
    public static final String EXTENSION_4 = asExtension(EXTENSION_RELATIONSHIP4, EXTENSION_DB4, EXTENSION_ID4);
    public static final List<String> EXTENSIONS = asList(String.format("%s,%s", EXTENSION_1, EXTENSION_2), String
            .format("%s,%s",EXTENSION_3, EXTENSION_4));

    public static AtomicLong rowNumberGenerator = new AtomicLong();

    private AnnotationDocMocker() {
    }

    public static AnnotationDocument createAnnotationDoc(String geneProductId) {
        AnnotationDocument doc = new AnnotationDocument();
        doc.geneProductId = geneProductId;

        // automatically compute a document identifier,
        // to overcome non-uniqueness of all other annotation fields
        // (in solrconfig.xml this is set automatically as a UUID)
        long rowNumber = rowNumberGenerator.incrementAndGet();
        doc.id = geneProductId + "!" + rowNumber;

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
        doc.taxonAncestors = TAXON_ANCESTORS;
        doc.targetSets = TARGET_SET;
        doc.geneProductSubset = GP_SUBSET;
        doc.goAspect = GO_ASPECT;
        doc.date = DATE;

        return doc;
    }

    public static AnnotationDocument createAnnotationDoc(String geneProductId, String goId) {
        AnnotationDocument doc = createAnnotationDoc(geneProductId);
        doc.goId = goId;
        return doc;
    }

    public static String asExtension(String relationship, String db, String id) {
        return String.format("%s(%s:%s)", relationship, db, id);
    }

    //----- Setup data ---------------------//

    public static List<AnnotationDocument> createGenericDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createGPId(i)))
                .collect(Collectors.toList());
    }

    public static List<AnnotationDocument> createGenericDocs(int n, Function<Integer, String> idCreator) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(idCreator.apply(i)))
                .collect(Collectors.toList());
    }

    public static List<AnnotationDocument> createGenericDocs(int n, Supplier<String> gpIdCreator) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(gpIdCreator.get()))
                .collect(Collectors.toList());
    }


    public static String createGPId(int idNum) {
        return String.format("A0A%03d", idNum);
    }

    public static String createUniProtGPID(int idNum) {
        return String.format("UniProtKB:A0A%03d", idNum);
    }
}
