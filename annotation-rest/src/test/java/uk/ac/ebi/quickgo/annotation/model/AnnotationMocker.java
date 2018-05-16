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
    public static final String DB = "ComplexPortal";
    public static final String ID = "CPX-1004";
    public static final String GO_ID = "GO:0003824";
    public static final String GO_NAME = "catalytic activity";
    public static final String DATE_AS_STRING = "20121002";
    private static final String SLIMMED_FROM_GO_ID = "GO:0071840";
    public static final List<String> SLIMMED_TO_IDS = Collections.singletonList(SLIMMED_FROM_GO_ID);
    private static final String COMMA = ",";
    private static final List<List<Supplier<Annotation.SimpleXRef>>> WITH_FROM = asList(
            singletonList(IPR_1), asList(IPR_2, IPR_3));
    private static final List<List<Supplier<Annotation.QualifiedXref>>> EXTENSIONS = asList(
            singletonList(OCCURS_IN_CL_1),
            asList(OCCURS_IN_CL_2, OCCURS_IN_CL_3));
    private static final String GENE_PRODUCT_ID = "ComplexPortal:CPX-1004";
    private static final String ASSIGNED_BY = "ComplexPortal";
    public static final String GO_ASPECT = "cellular_component";
    private static final Date DATE = Date.from(
            LocalDate.of(2012, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());
    public static final String SYNONYMS =
            "DR1:KAT14:KAT2B:MBIP:SGF29:TADA2A:TADA3:WDR5:YEATS2:ZZZ3,ADA2A-containing complex,Ada2/PCAF/Ada3 " +
                    "transcription activator complex,KAT2B-containing ATAC complex,ATAC complex,P-ATAC complex,Ada " +
                    "two A containing complex";
    public static final String NAME = "MoeA5";
    public static final String TYPE = "complex";

    public static Annotation createValidAnnotation() {
        Annotation annotation = new Annotation();
        annotation.id = DB + ":" + ID;
        annotation.setGeneProduct(GeneProduct.fromCurieId(annotation.id));
        annotation.extensions = connectedXrefs(EXTENSIONS);
        annotation.taxonId = TAXON_ID;
        annotation.goAspect = GO_ASPECT;     //todo is this populated
        annotation.goEvidence = GO_EVIDENCE;
        annotation.assignedBy = ASSIGNED_BY;
        annotation.date = DATE;
        annotation.evidenceCode = ECO_ID;
        annotation.geneProductId = GENE_PRODUCT_ID;
        annotation.qualifier = QUALIFIER;
        annotation.symbol = SYMBOL;
        annotation.reference = REFERENCE;
        annotation.withFrom = connectedXrefs(WITH_FROM);
        annotation.goId = GO_ID;
        annotation.interactingTaxonId = INTERACTING_TAXON_ID;
        annotation.goName = GO_NAME;
        annotation.taxonName = TAXON_NAME;
        annotation.name = NAME;
        annotation.synonyms = SYNONYMS;
        return annotation;
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

    enum FakeExtensionItem implements Supplier<Annotation.QualifiedXref> {
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

        @Override public Annotation.QualifiedXref get() {
            return new Annotation.QualifiedXref(db, id, qualifier);
        }

        @Override public String toString() {
            return qualifier + "(" + db + ":" + id + ")";
        }
    }
}
