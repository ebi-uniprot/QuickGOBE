package uk.ac.ebi.quickgo.annotation.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.FakeExtensionItem.OCCURS_IN_CL_1;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.FakeExtensionItem.OCCURS_IN_CL_2;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.FakeExtensionItem.OCCURS_IN_CL_3;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.FakeWithFromItem.IPR_1;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.FakeWithFromItem.IPR_2;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.FakeWithFromItem.IPR_3;

/**
 * A class for creating stubbed annotations, representing rows of data read from
 * annotation source files.
 *
 * Created 20/01/17
 * @author Tony
 */
public class AnnotationMocker {

    //Common
    private static final int NO_INTERACTING_TAXON_ID = 0;
    private static final String SLIMMED_FROM_GO_ID = "GO:0071840";
    private static final List<List<Supplier<Annotation.SimpleXRef>>> WITH_FROM =
            asList(singletonList(IPR_1), asList(IPR_2, IPR_3));
    private static final List<List<Supplier<Annotation.RelationXref>>> EXTENSIONS =
            asList(singletonList(OCCURS_IN_CL_1), asList(OCCURS_IN_CL_2, OCCURS_IN_CL_3));
    private static final Date DATE =
            Date.from(LocalDate.of(2012, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());

    //Test specific
    private static final String COMPLEX_PORTAL_PRODUCT_ID = "ComplexPortal:CPX-1004";
    private static final String UNIPROT_PRODUCT_ID_WITH_ISOFORM = "UniProtKB:Q4VCS5-2";
    private static final String UNIPROT_PRODUCT_ID_WITHOUT_ISOFORM = "UniProtKB:Q4VCS5";
    private static final String RNA_CENTAL_PRODUCT_ID = "RNAcentral:URS00000064B1_559292";

    //Common
    public static final String WITH_FROM_AS_STRING = IPR_1 + "|" + IPR_2 + "," + IPR_3;
    public static final String EXTENSIONS_AS_STRING = OCCURS_IN_CL_1 + "|" + OCCURS_IN_CL_2 + "," + OCCURS_IN_CL_3;
    public static final String SYMBOL = "atf4-creb1_mouse";
    public static final String QUALIFIER = "contributes_to";
    public static final String REFERENCE = "PMID:12871976";
    public static final String ECO_ID = "ECO:0000353";
    public static final String GO_EVIDENCE = "IPI";
    public static final int TAXON_ID = 12345;
    public static final String TAXON_NAME = "Hipdedipdiflorous";
    public static final int INTERACTING_TAXON_ID = 54321;
    public static final String GO_ID = "GO:0003824";
    public static final String GO_NAME = "catalytic activity";
    public static final String DATE_AS_STRING = "20121002";
    public static final List<String> SLIMMED_TO_IDS = Collections.singletonList(SLIMMED_FROM_GO_ID);
    public static final String GO_ASPECT = "cellular_component";
    public static final String SYNONYMS =
            "DR1:KAT14:KAT2B:MBIP:SGF29:TADA2A:TADA3:WDR5:YEATS2:ZZZ3,ADA2A-containing complex,Ada2/PCAF/Ada3 " +
                    "transcription activator complex,KAT2B-containing ATAC complex,ATAC complex,P-ATAC complex,Ada " +
                    "two A containing complex";
    public static final String NAME = "MoeA5";
    public static final String TYPE = "complex";
    public static final String ASSIGNED_BY = "Dorna";

    //UniProtKB
    public static final String DB_UNIPROTKB = "UniProtKB";
    public static final String ID_UNIPROTKB_WITH_ISOFORM = "Q4VCS5-2";
    public static final String ID_UNIPROTKB_WITHOUT_ISOFORM = "Q4VCS5";
    public static final String ID_UNIPROTKB_CANONICAL = "Q4VCS5";

    //ComplexPortal
    public static final String DB_COMPLEX_PORTAL = "ComplexPortal";
    public static final String ID_COMPLEX_PORTAL = "CPX-1004";

    //RNACentral
    public static final String DB_RNA_CENTRAL = "RNAcentral";
    public static final String ID_RNA_CENTRAL = "URS00000064B1_559292";
    //    String gpType = "miRNA";
    //    annotation.id = String.format("%s:%s", db, gpId);
    //        annotation.setGeneProduct(GeneProduct.fromCurieId(annotation.id));
    //    annotation.geneProductId = String.format("%s:%s", db, gpId);
    //    annotation.assignedBy = db;
    //    annotation.symbol = gpId;

    public static Annotation createValidComplexPortalAnnotation() {
        Annotation annotation = new Annotation();
        annotation.id = DB_COMPLEX_PORTAL + ":" + ID_COMPLEX_PORTAL;
        annotation.setGeneProduct(GeneProduct.fromCurieId(annotation.id));
        annotation.geneProductId = COMPLEX_PORTAL_PRODUCT_ID;
        populateCommon(annotation);
        return annotation;
    }

    public static Annotation createValidUniProtAnnotationWithoutIsoForm() {
        Annotation annotation = new Annotation();
        annotation.id = DB_UNIPROTKB + ":" + ID_UNIPROTKB_WITHOUT_ISOFORM;
        annotation.setGeneProduct(GeneProduct.fromCurieId(UNIPROT_PRODUCT_ID_WITHOUT_ISOFORM));
        annotation.geneProductId = UNIPROT_PRODUCT_ID_WITHOUT_ISOFORM;
        populateCommon(annotation);
        return annotation;
    }

    public static Annotation createValidUniProtAnnotationWithIsoForm() {
        Annotation annotation = new Annotation();
        annotation.id = DB_UNIPROTKB + ":" + ID_UNIPROTKB_WITH_ISOFORM;
        annotation.setGeneProduct(GeneProduct.fromCurieId(UNIPROT_PRODUCT_ID_WITH_ISOFORM));
        annotation.geneProductId = UNIPROT_PRODUCT_ID_WITH_ISOFORM;
        populateCommon(annotation);
        return annotation;
    }

    public static Annotation createValidRNACentralAnnotation() {
        Annotation annotation = new Annotation();
        annotation.id = DB_RNA_CENTRAL + ":" + ID_RNA_CENTRAL;
        annotation.setGeneProduct(GeneProduct.fromCurieId(RNA_CENTAL_PRODUCT_ID));
        annotation.geneProductId = RNA_CENTAL_PRODUCT_ID;
        populateCommon(annotation);
        return annotation;
    }

    private static void populateCommon(Annotation annotation) {
        annotation.extensions = connectedXrefs(EXTENSIONS);
        annotation.taxonId = TAXON_ID;
        annotation.goAspect = GO_ASPECT;
        annotation.goEvidence = GO_EVIDENCE;
        annotation.assignedBy = ASSIGNED_BY;
        annotation.date = DATE;
        annotation.evidenceCode = ECO_ID;
        annotation.qualifier = QUALIFIER;
        annotation.symbol = SYMBOL;
        annotation.reference = REFERENCE;
        annotation.withFrom = connectedXrefs(WITH_FROM);
        annotation.goId = GO_ID;
        annotation.interactingTaxonId = NO_INTERACTING_TAXON_ID;
        annotation.goName = GO_NAME;
        annotation.taxonName = TAXON_NAME;
        annotation.name = NAME;
        annotation.synonyms = SYNONYMS;
    }

    private static <T extends Annotation.AbstractXref> List<Annotation.ConnectedXRefs<T>> connectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream().map(itemList -> {
                    Annotation.ConnectedXRefs<T> xrefs = new Annotation.ConnectedXRefs<>();
                    itemList.stream().map(Supplier::get).forEach(xrefs::addXref);
                    return xrefs;
                }
        ).collect(Collectors.toList());
    }

    enum FakeWithFromItem implements Supplier<Annotation.SimpleXRef> {
        IPR_1("InterPro", "IPR015421"),
        IPR_2("InterPro", "IPR015422"),
        IPR_3("InterPro", "IPR015421");

        private final String db;
        private final String id;

        FakeWithFromItem(String db, String id) {
            this.db = db;
            this.id = id;
        }

        @Override public Annotation.SimpleXRef get() {
            return new Annotation.SimpleXRef(db, id);
        }

        @Override public String toString() {
            return db + ":" + id;
        }
    }

    enum FakeExtensionItem implements Supplier<Annotation.RelationXref> {
        OCCURS_IN_CL_1("occurs_in", "CL", "0000001"),
        OCCURS_IN_CL_2("occurs_in", "CL", "0000002"),
        OCCURS_IN_CL_3("occurs_in", "CL", "0000003");

        private final String qualifier;
        private final String db;
        private final String id;

        FakeExtensionItem(String qualifier, String db, String id) {
            this.qualifier = qualifier;
            this.db = db;
            this.id = id;
        }

        @Override public Annotation.RelationXref get() {
            return new Annotation.RelationXref(db, id, qualifier);
        }

        @Override public String toString() {
            return qualifier + "(" + db + ":" + id + ")";
        }
    }
}
