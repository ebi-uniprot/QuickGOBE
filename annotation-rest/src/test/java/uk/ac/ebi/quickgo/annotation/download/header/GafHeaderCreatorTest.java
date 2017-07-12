package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.Collections;
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
 * Time: 11:41
 * Created with IntelliJ IDEA.
 */
public class GafHeaderCreatorTest {

    private OntologyHeaderInfo mockOntology = mock(OntologyHeaderInfo.class);
    private ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private HeaderContent mockContent = mock(HeaderContent.class);

    @Before
    public void setup() {
        when(mockOntology.versions()).thenReturn(Collections.emptyList());
    }

    @Test
    public void versionIsGaf() throws Exception {
        GafHeaderCreator gafHeaderCreator = new GafHeaderCreator(mockOntology);

        gafHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send("!" + GafHeaderCreator.VERSION + "\n", MediaType.TEXT_PLAIN);
    }
}
