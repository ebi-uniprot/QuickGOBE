package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.GeneProduct;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImplTest.FakeExtensionItem
        .OCCURS_IN_CL_1;
import static uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImplTest.FakeExtensionItem
        .OCCURS_IN_CL_2;
import static uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImplTest.FakeExtensionItem
        .OCCURS_IN_CL_3;
import static uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImplTest.FakeWithFromItem.GO_1;
import static uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImplTest.FakeWithFromItem.GO_2;
import static uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverterImplTest.FakeWithFromItem.GO_3;

/**
 * Tests the implementation of the {@link AnnotationDocConverterImpl} class.
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 17:39
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationDocConverterImplTest {

    //Formatting
    private static final String COMMA = ",";

    //Test data
    private static final String ID = "1";
    private static final String GENE_PRODUCT_ID = "P99999";
    private static final String QUALIFIER = "enables";
    private static final String GO_ID = "GO:0000977";
    private static final int TAXON_ID = 2;
    private static final String ECO_ID = "ECO:0000353";
    private static final String ASSIGNED_BY = "InterPro";
    private static final String EXTENSIONS = OCCURS_IN_CL_1 + "|" + OCCURS_IN_CL_2 + "," + OCCURS_IN_CL_3;
    private static final List<String> TARGET_SETS = asList("KRUK", "BHF-UCL", "Exosome");
    private static final String SYMBOL = "moeA5";
    private static final String GO_ASPECT = "cellular_component";
    private static final Date DATE = Date.from(
            LocalDate.of(2012, 10, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());
    private static final String GENE_PRODUCT_TYPE = "protein";
    private static final int interactingTaxId = 3234;

    //Expected data
    private static final List<List<Supplier<Annotation.SimpleXRef>>> WITH_FROM =
            asList(singletonList(GO_1), asList(GO_2, GO_3));
    private static final List<List<Supplier<Annotation.QualifiedXref>>> EXTENSIONS_CONVERTED =
            asList(singletonList(OCCURS_IN_CL_1), asList(OCCURS_IN_CL_2, OCCURS_IN_CL_3));

    //Test input model
    private static final AnnotationDocument DOCUMENT = createStubDocument();

    //Instance to be tested
    private AnnotationDocConverter docConverter = new AnnotationDocConverterImpl();

    //Output model
    private Annotation model;

    @Before
    public void setUp() {
        model = docConverter.convert(DOCUMENT);
    }

    @Test
    public void convertIdSuccessfully() {
        assertThat(model.id, is(ID));
    }

    @Test
    public void convertGeneProductIdSuccessfully() {
        assertThat(model.geneProductId, is(GENE_PRODUCT_ID));
    }

    @Test
    public void convertQualifierSuccessfully() {
        assertThat(model.qualifier, is(QUALIFIER));
    }

    @Test
    public void convertGoIdSuccessfully() {
        assertThat(model.goId, is(GO_ID));
    }

    @Test
    public void convertECOIdSuccessfully() {
        assertThat(model.evidenceCode, is(ECO_ID));
    }

    @Test
    public void convertTaxonIdSuccessfully() {
        assertThat(model.taxonId, is(TAXON_ID));
    }

    @Test
    public void convertWithFromSuccessfully() {
        assertThat(model.withFrom, is(connectedXrefs(WITH_FROM)));
    }

    @Test
    public void convertNullWithFromSuccessfully() {
        AnnotationDocument doc = createStubDocument();
        doc.withFrom = null;

        Annotation model = docConverter.convert(doc);

        assertThat(model.withFrom, is(nullValue()));
    }

    @Test
    public void convertExtensionSuccessfully() {
        List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> convertedExtensions =
                connectedXrefs(EXTENSIONS_CONVERTED);

        assertThat(model.extensions, is(convertedExtensions));
    }

    @Test
    public void convertNullExtensionsSuccessfully() {
        AnnotationDocument doc = createStubDocument();
        doc.extensions = null;

        Annotation model = docConverter.convert(doc);
        assertThat(model.extensions, is(nullValue()));
    }

    @Test
    public void convertAssignedBySuccessfully() {
        assertThat(model.assignedBy, is(ASSIGNED_BY));
    }

    @Test
    public void convertSymbolSuccessfully() {
        assertThat(model.symbol, is(SYMBOL));
    }

    @Test
    public void convertNullAspectSuccessfully() {
        AnnotationDocument doc = createStubDocument();
        doc.goAspect = null;

        Annotation model = docConverter.convert(doc);
        assertThat(model.goAspect, is(nullValue()));
    }

    @Test
    public void convertAspectSuccessfully() {
        assertThat(model.goAspect, is(GO_ASPECT));
    }

    @Test
    public void convertsTargetSetsSuccessfully() {
        assertThat(model.targetSets, is(TARGET_SETS));
    }

    @Test
    public void convertsDateSuccessfully() {
        assertThat(model.date, is(DATE));
    }

    @Test
    public void convertsNullDateSuccessfully() {
        AnnotationDocument doc = createStubDocument();
        doc.date = null;

        Annotation model = docConverter.convert(doc);
        assertThat(model.date, is(nullValue()));
    }

    @Test
    public void convertsInteractingTaxIdSuccessfully() {
        assertThat(model.interactingTaxonId, is(interactingTaxId));
    }

    @Test
    public void createsCanonicalDateSuccessfully() {
        assertThat(model.canonicalId, is(GENE_PRODUCT_ID));
    }

    @Test
    public void populatesGeneProductModelSuccessfully() {
        assertThat(model.getGeneProduct(), is(notNullValue(GeneProduct.class)));
    }

    private static <T extends Annotation.AbstractXref> List<Annotation.ConnectedXRefs<T>> connectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream().map(itemList -> toConnectedXRefs(itemList)).collect(Collectors.toList());
    }

    private static <T extends Annotation.AbstractXref> Annotation.ConnectedXRefs<T> toConnectedXRefs
            (List<Supplier<T>> itemList) {
        Annotation.ConnectedXRefs<T> xrefs = new Annotation.ConnectedXRefs<>();
        itemList.stream().map(Supplier::get).forEach(xrefs::addXref);

        return xrefs;
    }

    private static <T extends Annotation.AbstractXref> List<String> stringsForConnectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream()
                .map(itemList ->
                        itemList.stream()
                                .map(Supplier::toString).collect(Collectors.joining(COMMA))
                ).collect(Collectors.toList());
    }

    private static AnnotationDocument createStubDocument() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.id = ID;
        doc.geneProductId = GENE_PRODUCT_ID;
        doc.qualifier = QUALIFIER;
        doc.goId = GO_ID;
        doc.taxonId = TAXON_ID;
        doc.evidenceCode = ECO_ID;
        doc.withFrom = stringsForConnectedXrefs(WITH_FROM);
        doc.assignedBy = ASSIGNED_BY;
        //        doc.extensions = stringsForConnectedXrefs(EXTENSIONS);
        doc.extensions = EXTENSIONS;
        doc.targetSets = TARGET_SETS;
        doc.symbol = SYMBOL;
        doc.goAspect = GO_ASPECT;
        doc.date = DATE;
        doc.geneProductType = GENE_PRODUCT_TYPE;
        doc.interactingTaxonId = interactingTaxId;
        return doc;
    }

    enum FakeWithFromItem implements Supplier<Annotation.SimpleXRef> {
        GO_1("GO", "0000001"),
        GO_2("GO", "0000002"),
        GO_3("GO", "0000003");

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
