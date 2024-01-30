package uk.ac.ebi.quickgo.annotation.download.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.annotation.download.TSVDownload.*;
import static uk.ac.ebi.quickgo.annotation.download.header.TSVHeaderCreator.*;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 12:10
 * Created with IntelliJ IDEA.
 */
class TSVHeaderCreatorTest {

    private static final String REQUEST_URI =
            "/QuickGO/services/annotation/downloadSearch?downloadLimit=7&geneProductId" +
                    "=UniProtKB:A0A000&includeFields=goName,taxonName";
    private static final List<String[]> fields2Columns = new ArrayList<>();
    private static final String DEFAULT_HEADER_STRING =
            GENE_PRODUCT_DB + "\t" + GENE_PRODUCT_ID + "\t" + SYMBOL + "\t" + QUALIFIER + "\t" + GO_TERM + "\t" +
                    GO_ASPECT + "\t" + ECO_ID + "\t" + GO_EVIDENCE_CODE + "\t" + REFERENCE + "\t" + WITH_FROM + "\t" +
                    TAXON_ID + "\t" + ASSIGNED_BY + "\t" + ANNOTATION_EXTENSION + "\t" + DATE + "\n";

    static {
        initialiseFieldColumns();
    }

    private final OntologyHeaderInfo mockOntology = mock(OntologyHeaderInfo.class);
    private final ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private final HeaderContent mockContent = mock(HeaderContent.class);
    private TSVHeaderCreator tsvHeaderCreator;

    @BeforeEach
    void setup() {
        String FORMAT_VERSION_1 = "test-version_1";
        String FORMAT_VERSION_2 = "test-version_2";
        tsvHeaderCreator = new TSVHeaderCreator();
        when(mockOntology.versions()).thenReturn(asList(FORMAT_VERSION_1, FORMAT_VERSION_2));
        when(mockContent.getUri()).thenReturn(REQUEST_URI);
        when(mockContent.getSelectedFields()).thenReturn(emptyList());
        when(mockContent.isSlimmed()).thenReturn(false);
    }

    @Test
    void writeColumnNameForIndividualField() throws Exception {
        for (String[] field2Column : fields2Columns) {
            verifyColumnNameForIndividualField(singletonList(field2Column[0]), field2Column[1] + "\n");
        }

        //Test gene product separately
        verifyColumnNameForIndividualField(singletonList(GENE_PRODUCT_FIELD_NAME),
                                         GENE_PRODUCT_DB + "\t" + GENE_PRODUCT_ID + "\n");
    }

    private void verifyColumnNameForIndividualField(List<String> selectedFields,
            String columnName) throws IOException {
        HeaderContent content = mock(HeaderContent.class);
        when(content.isSlimmed()).thenReturn(false);
        when(content.getSelectedFields()).thenReturn(selectedFields);
        ResponseBodyEmitter emitter = mock(ResponseBodyEmitter.class);

            tsvHeaderCreator.write(emitter, content);

            verify(emitter).send(columnName, MediaType.TEXT_PLAIN);
    }

    @Test
    void writeHeaderForSeveralSelectedFields() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(false);
        when(mockContent.getSelectedFields())
                .thenReturn(asList(GENE_PRODUCT_FIELD_NAME, GO_NAME_FIELD_NAME, TAXON_NAME_FIELD_NAME));

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(TSVHeaderCreator.GENE_PRODUCT_DB + "\t"
                + TSVHeaderCreator.GENE_PRODUCT_ID + "\t"
                + TSVHeaderCreator.GO_NAME + "\t"
                + TSVHeaderCreator.TAXON_NAME + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    void writeHeaderForSeveralSelectedFieldsInNewOrder() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(false);
        when(mockContent.getSelectedFields()).thenReturn(asList(TAXON_NAME_FIELD_NAME, GO_NAME_FIELD_NAME,
                GENE_PRODUCT_FIELD_NAME));
        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(TSVHeaderCreator.TAXON_NAME + "\t"
                                         + TSVHeaderCreator.GO_NAME + "\t"
                                         + TSVHeaderCreator.GENE_PRODUCT_DB + "\t"
                                         + TSVHeaderCreator.GENE_PRODUCT_ID
                                         + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    void ioErrorWhenWritingHeaderCausesIllegalStateException() throws Exception {
        doThrow(IllegalStateException.class).when(mockEmitter).send(any(), any());
        assertThrows(IllegalStateException.class, () -> tsvHeaderCreator.write(mockEmitter, mockContent));
    }

    @Test
    void writeHeaderForFullListOfFieldsNotSlimmed() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(false);
        when(mockContent.getSelectedFields()).thenReturn(Collections.emptyList());

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(DEFAULT_HEADER_STRING,
                                 MediaType.TEXT_PLAIN);
    }

    @Test
    void writeHeaderForSeveralSelectedFieldsWhenSlimmed() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(true);
        when(mockContent.getSelectedFields()).thenReturn(asList(GENE_PRODUCT_FIELD_NAME,
                                                                GO_TERM_FIELD_NAME,
                                                                TAXON_NAME_FIELD_NAME));

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(GENE_PRODUCT_DB + "\t"
                                 + GENE_PRODUCT_ID + "\t"
                + GO_TERM + "\t"
                + SLIMMED_FROM + "\t"
                + TAXON_NAME + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    void exceptionThrownIfEmitterIsNull() {
        assertThrows(IllegalArgumentException.class, () -> tsvHeaderCreator.write(null, mockContent));
    }

    @Test
    void exceptionThrownIfContentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> tsvHeaderCreator.write(mockEmitter, null));
    }

    @Test
    void noExceptionThrownIfEmitterThrowsIOException() throws Exception{
        doThrow(new IOException("Test IOException")).when(mockEmitter).send(any(Object.class), eq(MediaType
                                                                                                          .TEXT_PLAIN));
        tsvHeaderCreator.write(mockEmitter, mockContent);
    }

    private static void initialiseFieldColumns() {
        fields2Columns.add(new String[]{SYMBOL_FIELD_NAME, SYMBOL});
        fields2Columns.add(new String[]{QUALIFIER_FIELD_NAME, QUALIFIER});
        fields2Columns.add(new String[]{GO_TERM_FIELD_NAME, GO_TERM});
        fields2Columns.add(new String[]{GO_ASPECT_FIELD_NAME, GO_ASPECT});
        fields2Columns.add(new String[]{GO_NAME_FIELD_NAME, GO_NAME});
        fields2Columns.add(new String[]{ECO_ID_FIELD_NAME, ECO_ID});
        fields2Columns.add(new String[]{GO_EVIDENCE_CODE_FIELD_NAME, GO_EVIDENCE_CODE});
        fields2Columns.add(new String[]{REFERENCE_FIELD_NAME, REFERENCE});
        fields2Columns.add(new String[]{WITH_FROM_FIELD_NAME, WITH_FROM});
        fields2Columns.add(new String[]{TAXON_ID_FIELD_NAME, TAXON_ID});
        fields2Columns.add(new String[]{ASSIGNED_BY_FIELD_NAME, ASSIGNED_BY});
        fields2Columns.add(new String[]{ANNOTATION_EXTENSION_FIELD_NAME, ANNOTATION_EXTENSION});
        fields2Columns.add(new String[]{DATE_FIELD_NAME, DATE});
        fields2Columns.add(new String[]{TAXON_NAME_FIELD_NAME, TAXON_NAME});
        fields2Columns.add(new String[]{GENE_PRODUCT_NAME_FIELD_NAME, GENE_PRODUCT_NAME});
        fields2Columns.add(new String[]{GENE_PRODUCT_SYNONYMS_FIELD_NAME, GENE_PRODUCT_SYNONYMS});
        fields2Columns.add(new String[]{GENE_PRODUCT_TYPE_FIELD_NAME, GENE_PRODUCT_TYPE});
        fields2Columns.add(new String[]{INTERACTING_TAXON_ID_FIELD_NAME, INTERACTING_TAXON_ID});
    }
}
