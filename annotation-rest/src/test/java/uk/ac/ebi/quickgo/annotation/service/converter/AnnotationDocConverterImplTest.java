package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
    private static final String ID = "1";
    private static final String GENE_PRODUCT_ID = "P99999";
    private static final String QUALIFIER = "enables";
    private static final String GO_ID = "GO:0000977";
    private static final int TAXON_ID = 2;
    private static final String ECO_ID = "ECO:0000353";
    private static final List<String> WITH_FROM = Arrays.asList("GO:0036376", "GO:1990573");
    private static final String ASSIGNED_BY = "InterPro";
    private static final List<String> EXTENSIONS = Arrays.asList("occurs_in(CL:1000428)", "occurs_in(CL:1000429)");

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
        assertThat(model.ecoId, is(ECO_ID));
    }

    @Test
    public void convertTaxonIdSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.taxonId, is(TAXON_ID));
    }

    @Test
    public void convertWithFromSuccessfully() {
        Annotation model = docConverter.convert(DOCUMENT);
        assertThat(model.withFrom, is(WITH_FROM));
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

        return doc;
    }
}
