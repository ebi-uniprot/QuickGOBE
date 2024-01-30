package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class GafHeaderCreatorTest {

    private final OntologyHeaderInfo mockOntology = mock(OntologyHeaderInfo.class);
    private final ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private final HeaderContent mockContent = mock(HeaderContent.class);

    @BeforeEach
    void setup() {
        when(mockOntology.versions()).thenReturn(Collections.emptyList());
    }

    @Test
    void versionIsGaf() throws Exception {
        GafHeaderCreator gafHeaderCreator = new GafHeaderCreator(mockOntology);

        gafHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send("!" + GafHeaderCreator.VERSION + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    void gafGeneratedByUniprot() throws Exception {
        GafHeaderCreator gafHeaderCreator = new GafHeaderCreator(mockOntology);

        gafHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send("!" + GafHeaderCreator.GENERATED_BY + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    void gafGeneratedDateIsTodate() throws Exception {
        GafHeaderCreator gafHeaderCreator = new GafHeaderCreator(mockOntology);

        when(mockContent.getDate()).thenReturn("2021-02-03");
        gafHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send("!" + GafHeaderCreator.DATE_GENERATED + "2021-02-03\n", MediaType.TEXT_PLAIN);
    }
}
