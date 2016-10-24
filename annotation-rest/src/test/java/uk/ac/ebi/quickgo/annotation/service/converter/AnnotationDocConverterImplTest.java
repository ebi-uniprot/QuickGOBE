package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the implementation of the {@link AnnotationDocConverterImpl} class.
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 17:39
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationDocConverterImplTest {
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String ID = "1";
    private static final String GENE_PRODUCT_ID = "P99999";
    private static final String QUALIFIER = "enables";
    private static final String GO_ID = "GO:0000977";
    private static final int TAXON_ID = 2;
    private static final String ECO_ID = "ECO:0000353";
    private static final List<String> WITH_FROM = asList("GO:0036376", "GO:1990573,GO:1990573");
    private static final String ASSIGNED_BY = "InterPro";
    private static final List<String> EXTENSIONS = asList("occurs_in(CL:1000428)", "occurs_in(CL:1000429),occurs_in" +
            "(CL:1000430)");
    private static final List<String> TARGET_SETS = asList("KRUK", "BHF-UCL", "Exosome");
    private static final String SYMBOL = "moeA5";
    private static final String GO_ASPECT = "cellular_component";
    private static final AnnotationDocument DOCUMENT = createStubDocument();
    private AnnotationDocConverter docConverter;

    @Before
    public void setUp() throws Exception {
        docConverter = new AnnotationDocConverterImpl();
    }

    @Test
    public void convertIdSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.id, is(ID));
    }

    @Test
    public void convertGeneProductIdSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.geneProductId, is(GENE_PRODUCT_ID));
    }

    @Test
    public void convertQualifierSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.qualifier, is(QUALIFIER));
    }

    @Test
    public void convertGoIdSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.goId, is(GO_ID));
    }

    @Test
    public void convertECOIdSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.evidenceCode, is(ECO_ID));
    }

    @Test
    public void convertTaxonIdSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.taxonId, is(TAXON_ID));
    }

    @Test
    public void convertWithFromSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.withFrom, is(asConnectedXrefList(WITH_FROM)));
    }

    @Test
    public void convertNullWithFromSuccessfully() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.withFrom = null;

        Annotation model = docConverter.convert(doc);
        assertThat(model.withFrom, is(nullValue()));
    }

    @Test
    public void convertExtensionSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.extensions, is(EXTENSIONS));
    }

    @Test
    public void convertNullExtensionsSuccessfully() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.extensions = null;

        Annotation model = docConverter.convert(doc);
        assertThat(model.extensions, is(nullValue()));
    }

    @Test
    public void convertAssignedBySuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.assignedBy, is(ASSIGNED_BY));
    }

    @Test
    public void convertSymbolSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.symbol, is(SYMBOL));
    }

    @Test
    public void convertNullAspectSuccessfully() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.goAspect = null;

        Annotation model = docConverter.convert(doc);
        assertThat(model.goAspect, is(nullValue()));
    }

    @Test
    public void convertAspectSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.goAspect, is(GO_ASPECT));
    }

    @Test
    public void convertsTargetSetsSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.targetSets, is(TARGET_SETS));
    }

    private static AnnotationDocument createStubDocument() {
        AnnotationDocument doc = new AnnotationDocument();
        doc.id = ID;
        doc.geneProductId = GENE_PRODUCT_ID;
        doc.qualifier = QUALIFIER;
        doc.goId = GO_ID;
        doc.taxonId = TAXON_ID;
        doc.evidenceCode = ECO_ID;
        doc.withFrom = WITH_FROM;
        doc.assignedBy = ASSIGNED_BY;
        doc.extensions = EXTENSIONS;
        doc.targetSets = TARGET_SETS;
        doc.symbol = SYMBOL;
        doc.goAspect = GO_ASPECT;

        return doc;
    }

    private List<Annotation.ConnectedXRefs> asConnectedXrefList(List<String> csvs) {
        if (csvs != null && !csvs.isEmpty()) {
            return csvs.stream()
                    .map(csv -> {
                        Annotation.ConnectedXRefs<Annotation.SimpleXRef> connectedXRefs =
                                new Annotation.ConnectedXRefs<>();

                        Stream.of(csv.split(COMMA))
                                .forEach(xref -> {
                                    String[] dbAndSig = extractDBAndSignature(xref);
                                    connectedXRefs.addXref(new Annotation.SimpleXRef(dbAndSig[0], dbAndSig[1]));
                                });
                        return connectedXRefs;
                    }
            ).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private String[] extractDBAndSignature(String xref) {
        int colonPos = xref.indexOf(COLON);

        String database;
        String signature;

        if (colonPos == -1) {
            database = xref;
            signature = null;
        } else {
            database = xref.substring(0, colonPos);
            signature = xref.substring(colonPos + 1, xref.length());
        }

        return new String[]{database, signature};
    }
}
