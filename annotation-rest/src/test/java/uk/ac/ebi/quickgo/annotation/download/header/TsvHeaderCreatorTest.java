package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToTSV.*;
import static uk.ac.ebi.quickgo.annotation.download.header.TsvHeaderCreator.*;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 12:10
 * Created with IntelliJ IDEA.
 */
public class TsvHeaderCreatorTest {

    private static final String REQUEST_URI =
            "/QuickGO/services/annotation/downloadSearch?downloadLimit=7&geneProductId" +
                    "=UniProtKB:A0A000&includeFields=goName,taxonName";
    private Ontology mockOntology = mock(Ontology.class);
    private ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private HeaderContent mockContent = mock(HeaderContent.class);

    private static List<String[]> fields2Columns = new ArrayList<>();
    static {
        fields2Columns.add(new String[]{GENE_PRODUCT_ID_FIELD_NAME,GENE_PRODUCT_ID});
        fields2Columns.add(new String[]{SYMBOL_FIELD_NAME,SYMBOL});
        fields2Columns.add(new String[]{QUALIFIER_FIELD_NAME,QUALIFIER});
        fields2Columns.add(new String[]{GO_TERM_FIELD_NAME,GO_TERM});
        fields2Columns.add(new String[]{GO_NAME_FIELD_NAME,GO_NAME});
        fields2Columns.add(new String[]{ECO_ID_FIELD_NAME,ECO_ID});
        fields2Columns.add(new String[]{GO_EVIDENCE_CODE_FIELD_NAME,GO_EVIDENCE_CODE});
        fields2Columns.add(new String[]{REFERENCE_FIELD_NAME,REFERENCE});
        fields2Columns.add(new String[]{WITH_FROM_FIELD_NAME,WITH_FROM});
        fields2Columns.add(new String[]{TAXON_ID_FIELD_NAME,TAXON_ID});
        fields2Columns.add(new String[]{ASSIGNED_BY_FIELD_NAME,ASSIGNED_BY});
        fields2Columns.add(new String[]{ANNOTATION_EXTENSION_FIELD_NAME,ANNOTATION_EXTENSION});
        fields2Columns.add(new String[]{DATE_FIELD_NAME,DATE});
        fields2Columns.add(new String[]{TAXON_NAME_FIELD_NAME,TAXON_NAME});
    }

    @Before
    public void setup() {
        when(mockContent.uri()).thenReturn(REQUEST_URI);
        String FORMAT_VERSION_1 = "test-version_1";
        String FORMAT_VERSION_2 = "test-version_2";
        when(mockOntology.versions()).thenReturn(asList(FORMAT_VERSION_1, FORMAT_VERSION_2));
    }

    @Test
    public void writeColumnNameForIndividualField() throws Exception {
        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();
        for(String[] field2Column : fields2Columns){
            HeaderContent content = mock(HeaderContent.class);
            when(content.isSlimmed()).thenReturn(false);
            when(content.selectedFields()).thenReturn(Collections.singletonList(field2Column[0]));
            ResponseBodyEmitter emitter = mock(ResponseBodyEmitter.class);

            tsvHeaderCreator.write(emitter, content);

            verify(emitter).send(field2Column[1] + "\n", MediaType.TEXT_PLAIN);
        }
    }

    @Test
    public void writeHeaderForSeveralSelectedFields() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(false);
        when(mockContent.selectedFields()).thenReturn(asList(GENE_PRODUCT_ID_FIELD_NAME, GO_NAME_FIELD_NAME, TAXON_NAME_FIELD_NAME));

        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(TsvHeaderCreator.GENE_PRODUCT_ID + "\t"
                                         + TsvHeaderCreator.GO_NAME + "\t"
                                         + TsvHeaderCreator.TAXON_NAME + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    public void writeHeaderForFullListOfFieldsUnslimmed() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(false);
        when(mockContent.selectedFields()).thenReturn(Collections.emptyList());

        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(GENE_PRODUCT_ID + "\t"
                                         + SYMBOL + "\t"
                                         + QUALIFIER + "\t"
                                         + GO_TERM + "\t"
                                         + GO_NAME + "\t"
                                         + ECO_ID + "\t"
                                         + GO_EVIDENCE_CODE + "\t"
                                         + REFERENCE + "\t"
                                         + WITH_FROM + "\t"
                                         + TAXON_ID + "\t"
                                         + ASSIGNED_BY + "\t"
                                         + ANNOTATION_EXTENSION + "\t"
                                         + DATE + "\t"
                                         + TAXON_NAME + "\n",
                                 MediaType.TEXT_PLAIN);
    }

    @Test
    public void writeHeaderForSeveralSelectedFieldsWhenSlimmed() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(true);
        when(mockContent.selectedFields()).thenReturn(asList(GENE_PRODUCT_ID_FIELD_NAME,
                                                             GO_TERM_FIELD_NAME,
                                                             TAXON_NAME_FIELD_NAME));

        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(GENE_PRODUCT_ID + "\t"
                                         + GO_TERM + "\t"
                                         + SLIMMED_FROM + "\t"
                                         + TAXON_NAME + "\n", MediaType.TEXT_PLAIN);
    }


    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfEmitterIsNull(){
        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();
        tsvHeaderCreator.write(null, mockContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfContentIsNull(){
        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();
        tsvHeaderCreator.write(mockEmitter, null);
    }
}
