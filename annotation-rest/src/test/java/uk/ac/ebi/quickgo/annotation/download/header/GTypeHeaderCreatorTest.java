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
public class GTypeHeaderCreatorTest {

    private static final String DATE = "2017-05-23";
    private static final String REQUEST_URI =
            "/QuickGO/services/annotation/downloadSearch?downloadLimit=7&geneProductId" +
                    "=UniProtKB:A0A000&includeFields=goName,taxonName";
    private final String FORMAT_VERSION_1 = "test-version_1";
    private final String FORMAT_VERSION_2 = "test-version_2";
    private OntologyHeaderInfo mockOntology = mock(OntologyHeaderInfo.class);
    private ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private HeaderContent mockContent = mock(HeaderContent.class);

    @Before
    public void setup() {
        when(mockContent.getDate()).thenReturn(DATE);
        when(mockContent.getUri()).thenReturn(REQUEST_URI);
        when(mockOntology.versions()).thenReturn(Arrays.asList(FORMAT_VERSION_1, FORMAT_VERSION_2));
    }

    @Test
    public void writeIsComplete() throws Exception {
        GTypeHeaderCreator gTypeHeaderCreator = new TestGTypeHeaderCreator(mockOntology);

        gTypeHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + TestGTypeHeaderCreator.VERSION + "\n",
                MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + GTypeHeaderCreator.PROJECT_NAME + "\n",
                MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + GTypeHeaderCreator.URL + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + GTypeHeaderCreator.EMAIL + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + GTypeHeaderCreator.DATE + DATE + "\n", MediaType
                .TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + FORMAT_VERSION_1 + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + FORMAT_VERSION_2 + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + GTypeHeaderCreator.FILTERS_INTRO + "\n", MediaType
                .TEXT_PLAIN);
        verify(mockEmitter).send(GTypeHeaderCreator.PREFIX + REQUEST_URI + "\n", MediaType
                .TEXT_PLAIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfOntologyIsNull() {
        new TestGTypeHeaderCreator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfEmitterIsNull() {
        GTypeHeaderCreator gTypeHeaderCreator = new TestGTypeHeaderCreator(mockOntology);
        gTypeHeaderCreator.write(null, mockContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfContentIsNull() {
        GTypeHeaderCreator gTypeHeaderCreator = new TestGTypeHeaderCreator(mockOntology);
        gTypeHeaderCreator.write(mockEmitter, null);
    }

    private static class TestGTypeHeaderCreator extends GTypeHeaderCreator {
        final static String VERSION = "TEST";

        TestGTypeHeaderCreator(OntologyHeaderInfo ontology) {
            super(ontology);
        }

        @Override String version() {
            return VERSION;
        }
    }
}
