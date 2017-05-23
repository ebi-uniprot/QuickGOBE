package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 12:10
 * Created with IntelliJ IDEA.
 */
public class TsvHeaderCreatorTest {

    private static final String DATE = "2017-05-23";
    private static final String REQUEST_URI =
            "/QuickGO/services/annotation/downloadSearch?downloadLimit=7&geneProductId" +
                    "=UniProtKB:A0A000&includeFields=goName,taxonName";
    private final String FORMAT_VERSION_1 = "test-version_1";
    private final String FORMAT_VERSION_2 = "test-version_2";
    private Ontology mockOntology = mock(Ontology.class);
    private ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private HeaderContent mockContent = mock(HeaderContent.class);

    @Before
    public void setup() {


        when(mockContent.uri()).thenReturn(REQUEST_URI);
        when(mockOntology.versions()).thenReturn(Arrays.asList(FORMAT_VERSION_1, FORMAT_VERSION_2));
    }

    @Test
    public void writeIsForSlimmedResults() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(true);
        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(TsvHeaderCreator.TSV_COL_HEADINGS_INCLUDING_SLIM + "\n",
                                 MediaType.TEXT_PLAIN);
    }

    @Test
    public void writeIsForNonSlimmedResults() throws Exception {
        when(mockContent.isSlimmed()).thenReturn(false);
        TsvHeaderCreator tsvHeaderCreator = new TsvHeaderCreator();

        tsvHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(TsvHeaderCreator.TSV_COL_HEADINGS_EXCLUDING_SLIM + "\n",
                                 MediaType.TEXT_PLAIN);
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
