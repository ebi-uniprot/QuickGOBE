package uk.ac.ebi.quickgo.annotation.model;

import java.time.LocalDate;
import java.time.ZoneId;
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

    private static final String COMMA = ",";
    private static final List<List<Supplier<Annotation.SimpleXRef>>> WITH_FROM = asList(
            singletonList(IPR_1), asList(IPR_2, IPR_3));
    public static final String WITH_FROM_AS_STRING = IPR_1 + "|" + IPR_2 + "," + IPR_3;
    private static final List<List<Supplier<Annotation.QualifiedXref>>> EXTENSIONS = asList(
            singletonList(OCCURS_IN_CL_1),
            asList(OCCURS_IN_CL_2, OCCURS_IN_CL_3));
    public static final String EXTENSIONS_AS_STRING = OCCURS_IN_CL_1 + "|" + OCCURS_IN_CL_2 + "," + OCCURS_IN_CL_3;
    public static final String SYMBOL = "atf4-creb1_mouse";
    public static final String QUALIFIER = "enables";
    public static final String REFERENCE = "PMID:12871976";
    private static final String GENE_PRODUCT_ID = "IntAct:EBI-10043081";
    public static final String EVIDENCE_CODE = "ECO:0000353";
    private static final String ASSIGNED_BY = "IntAct";
    public static final String GO_EVIDENCE = "IPI";
    private static final String GO_ASPECT = "molecular_function";
    public static final int TAXON_ID = 12345;
    public static final String INTERACTING_TAXON_ID = "54321";
    public static final String DB = "IntAct";
    public static final String ID = "EBI-10043081";
    public static final String GO_ID = "GO:0003824";
    private static final Date DATE = Date.from(
            LocalDate.of(2012, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());
    public static final String DATE_AS_STRING = "20121002";


    public static Annotation createValidAnnotation() {
        Annotation annotation = new Annotation();
        annotation.id = DB + ":" + ID;
        annotation.extensions = connectedQualifiedXrefs(EXTENSIONS);
        annotation.taxonId  = TAXON_ID;
        annotation.goAspect = GO_ASPECT;     //todo is this populated
        annotation.goEvidence = GO_EVIDENCE;
        annotation.assignedBy = ASSIGNED_BY;
        annotation.date = DATE;
        annotation.evidenceCode = EVIDENCE_CODE;
        annotation.geneProductId = GENE_PRODUCT_ID;
        annotation.qualifier = QUALIFIER;
        annotation.symbol = SYMBOL;
        annotation.reference = REFERENCE;
        annotation.withFrom = connectedSimpleXrefs(WITH_FROM);
        annotation.goId = GO_ID;
        annotation.interactingTaxonId = INTERACTING_TAXON_ID;
        return annotation;
    }

    private static List<Annotation.ConnectedXRefs<Annotation.SimpleXRef>> connectedSimpleXrefs(
            List<List<Supplier<Annotation.SimpleXRef>>> items) {
        return items.stream().map(itemList -> {
                                      Annotation.ConnectedXRefs<Annotation.SimpleXRef> xrefs = new Annotation.ConnectedXRefs<>();
                                      itemList.stream().map(Supplier::get).forEach(xrefs::addXref);
                                      return xrefs;
                                  }
        ).collect(Collectors.toList());
    }

    private static List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedQualifiedXrefs(
            List<List<Supplier<Annotation.QualifiedXref>>> items) {
        return items.stream().map(itemList -> {
                                      Annotation.ConnectedXRefs<Annotation.QualifiedXref> xrefs = new Annotation.ConnectedXRefs<>();
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

    private static <T extends Annotation.AbstractXref> List<String> stringsForConnectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream()
                    .map(itemList ->
                                 itemList.stream()
                                         .map(Supplier::toString).collect(Collectors.joining(COMMA))
                    ).collect(Collectors.toList());
    }
}
