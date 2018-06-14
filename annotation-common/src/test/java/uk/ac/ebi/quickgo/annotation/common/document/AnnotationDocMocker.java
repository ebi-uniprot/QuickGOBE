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
    public static AtomicLong rowNumberGenerator = new AtomicLong();
    public static final String GO_ID = "GO:0003824";
    public static final String ECO_ID = "ECO:0000256";
    public static final String REFERENCE = "GO_REF:0000002";
    public static final String TAXON_ID = "12345";
    public static final String GO_ASPECT = "cellular_component";
    private static final String EXTENSION_DB1 = "NCBI_gi";
    private static final String EXTENSION_DB2 = "Cl";
    private static final String EXTENSION_DB3 = "UBERON-AG";
    private static final String EXTENSION_DB4 = "PO1234";
    private static final String EXTENSION_ID1 = "0001675";
    private static final String EXTENSION_ID2 = "AbC:0032";
    private static final String EXTENSION_ID3 = "000-6000";
    private static final String EXTENSION_ID4 = "QWE_90hy";
    private static final String EXTENSION_RELATIONSHIP1 = "results_in_development_of";
    private static final String EXTENSION_RELATIONSHIP2 = "acts_on_population_of";
    private static final String EXTENSION_RELATIONSHIP3 = "indicative_of";
    private static final String EXTENSION_RELATIONSHIP4 = "happy_about";
    private static final String EXTENSION_1 = asExtension(EXTENSION_RELATIONSHIP1, EXTENSION_DB1, EXTENSION_ID1);
    private static final String EXTENSION_2 = asExtension(EXTENSION_RELATIONSHIP2, EXTENSION_DB2, EXTENSION_ID2);
    private static final String EXTENSION_3 = asExtension(EXTENSION_RELATIONSHIP3, EXTENSION_DB3, EXTENSION_ID3);
    private static final String EXTENSION_4 = asExtension(EXTENSION_RELATIONSHIP4, EXTENSION_DB4, EXTENSION_ID4);
    public static final String EXTENSIONS = EXTENSION_1 + "," + EXTENSION_2 + "|" + EXTENSION_3 + "," + EXTENSION_4;

    private static final Date DATE = Date.from(
            LocalDate.of(1869, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());
    private static final String OBJECT_TYPE = "protein";
    private static final List<Integer> TAXON_ANCESTORS = asList(12345, 1234, 123, 12, 1);
    private static final String QUALIFIER = "enables";
    private static final String GO_EVIDENCE = "IEA";
    private static final List<String> TARGET_SET = asList("KRUK", "BHF-UCL", "Exosome");
    private static final String GP_SUBSET = "TrEMBL";
    private static final List<String> WITH_FROM = asList("InterPro:IPR015421", "InterPro:IPR015422");
    private static final int INTERACTING_TAXON_ID = 35758;
    private static final String ASSIGNED_BY = "InterPro";
    private static final String SYMBOL = "moeA5";
    private static final String PROTEOME = "none";

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
        doc.taxonId = Integer.parseInt(TAXON_ID);
        doc.taxonAncestors = TAXON_ANCESTORS;
        doc.targetSets = TARGET_SET;
        doc.geneProductSubset = GP_SUBSET;
        doc.goAspect = GO_ASPECT;
        doc.date = DATE;
        doc.proteome = PROTEOME;

        return doc;
    }

    public static AnnotationDocument createAnnotationDoc(String geneProductId, String goId) {
        AnnotationDocument doc = createAnnotationDoc(geneProductId);
        doc.goId = goId;
        return doc;
    }

    private static String asExtension(String relationship, String db, String id) {
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

    /**
     * Create a list of {@link AnnotationDocument} where the GO id is not constant but varies.
     * @param number the square of which will be the number of documents created. The number of docs created will be
     * number (each with same GO id) * number.
     * @return list of annotation documents generated.
     */
    public static List<AnnotationDocument> createGenericDocsChangingGoId(int number) {

        return IntStream.range(0, number)
                        .mapToObj(i -> createGenericDocs(number, createUniProtGPID(i)))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
    }

    /**
     * Create a list of {@link AnnotationDocument} for the supplied gene product id, and generating a GO id based on
     * the number of documents to create.
     * @param number of documents to create
     * @param gpId a gene product id
     * @return list of annotation documents generated.
     */
    public static List<AnnotationDocument> createGenericDocs(int number, String gpId) {
        return IntStream.range(0, number)
                        .mapToObj(i -> {
                            AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(gpId);
                            doc.goId = createGOID(i);
                            return doc;
                        })
                        .collect(Collectors.toList());
    }

    public static List<AnnotationDocument> createGenericDocs(int n, Supplier<String> gpIdCreator) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(gpIdCreator.get()))
                .collect(Collectors.toList());
    }


    private static String createGPId(int idNum) {
        return String.format("A0A%03d", idNum);
    }

    public static String createUniProtGPID(int idNum) {
        return String.format("UniProtKB:A0A%03d", idNum);
    }

    /**
     * Generate a GO id.
     * @param idNum the number to be formatted as a GO id
     * @return GO id
     */
    private static String createGOID(int idNum) {
        return String.format("GO:%07d", idNum);
    }
}
