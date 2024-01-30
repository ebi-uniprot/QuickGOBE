package uk.ac.ebi.quickgo.annotation.download.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 12:10
 * Created with IntelliJ IDEA.
 */
class GeneTypeHeaderCreatorTest {

    private static final String DATE = "2017-05-23";
    private static final String REQUEST_URI =
            "https://www.ebi.ac.uk/QuickGO/annotations?downloadLimit=7&geneProductId" +
                    "=UniProtKB:A0A000&includeFields=goName,taxonName";
    private final String FORMAT_VERSION_1 = "test-version_1";
    private final String FORMAT_VERSION_2 = "test-version_2";
    private OntologyHeaderInfo mockOntology = mock(OntologyHeaderInfo.class);
    private ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private HeaderContent mockContent = mock(HeaderContent.class);
    private GeneTypeHeaderCreator gTypeHeaderCreator;

    @BeforeEach
    void setup() {
        when(mockContent.getDate()).thenReturn(DATE);
        when(mockContent.getUri()).thenReturn(REQUEST_URI);
        when(mockOntology.versions()).thenReturn(Arrays.asList(FORMAT_VERSION_1, FORMAT_VERSION_2));
        gTypeHeaderCreator = new TestGTypeHeaderCreator(mockOntology);
    }

    @Test
    void writeIsComplete() throws Exception {
        GeneTypeHeaderCreator gTypeHeaderCreator = new TestGTypeHeaderCreator(mockOntology);

        gTypeHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + TestGTypeHeaderCreator.VERSION + "\n",
                MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + GeneTypeHeaderCreator.PROJECT_NAME + "\n",
                MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + GeneTypeHeaderCreator.URL + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + GeneTypeHeaderCreator.EMAIL + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + GeneTypeHeaderCreator.DATE + DATE + "\n", MediaType
                .TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + FORMAT_VERSION_1 + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + FORMAT_VERSION_2 + "\n", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + GeneTypeHeaderCreator.FILTERS_INTRO + "\n", MediaType
                .TEXT_PLAIN);
        verify(mockEmitter).send(GeneTypeHeaderCreator.PREFIX + REQUEST_URI + "\n", MediaType
                .TEXT_PLAIN);
    }

    @Test
    void exceptionThrownIfOntologyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new TestGTypeHeaderCreator(null));
    }

    @Test
    void exceptionThrownIfEmitterIsNull() {
        assertThrows(IllegalArgumentException.class, () -> gTypeHeaderCreator.write(null, mockContent));
    }

    @Test
    void exceptionThrownIfContentIsNull() {
        assertThrows(IllegalArgumentException.class, () -> gTypeHeaderCreator.write(mockEmitter, null));
    }

    @Test
    void noExceptionThrownIfEmitterThrowsIOException() throws Exception{
        doThrow(new IOException("Test IOException")).when(mockEmitter).send(any(Object.class), eq(MediaType
                                                                                                          .TEXT_PLAIN));
        gTypeHeaderCreator.write(mockEmitter, mockContent);
    }

    private static class TestGTypeHeaderCreator extends GeneTypeHeaderCreator {
        final static String VERSION = "TEST";

        TestGTypeHeaderCreator(OntologyHeaderInfo ontology) {
            super(ontology);
        }

        @Override String version() {
            return VERSION;
        }
    }
}
