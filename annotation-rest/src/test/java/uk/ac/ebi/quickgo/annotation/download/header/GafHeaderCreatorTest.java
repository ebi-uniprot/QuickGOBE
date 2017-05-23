package uk.ac.ebi.quickgo.annotation.download.header;

import uk.ac.ebi.quickgo.annotation.download.header.GafHeaderCreator;
import uk.ac.ebi.quickgo.annotation.download.header.HeaderContent;
import uk.ac.ebi.quickgo.annotation.download.header.Ontology;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 11:41
 * Created with IntelliJ IDEA.
 */
public class GafHeaderCreatorTest {

    private Ontology mockOntology = mock(Ontology.class);
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
