package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig;
import uk.ac.ebi.quickgo.annotation.validation.service.JobTestRunnerConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 17:00
 * Created with IntelliJ IDEA.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(class = loader = SpringApplicationContextLoader.class)
public class HeaderTest {

    public static final String FILE_LOC = "C:\\Users\\twardell\\IdeaProjects\\QuickGOBE\\annotation-rest\\src\\test" +
                                          "\\resources" +
                                          "/ONTOLOGY_IRI.dat.gz";
    public static final String URI = "/QuickGO/services/annotation/search";
    @Value("${download.ontology.source}")
    private Resource[] resources;

    private static final Map<String, String[]> mockParameterMap = new HashMap<>();
    static {
        mockParameterMap.put("assignedBy", new String[] {"foo","bar"} );

    }

    @Mock ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    @Mock HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    @Mock MediaType mockMediaType = mock(MediaType.class);

    @Before
    public void setup(){
        when(mockMediaType.getSubtype()).thenReturn("GAF");
        when(mockRequest.getRequestURI()).thenReturn(URI);
        when(mockRequest.getParameterMap()).thenReturn(mockParameterMap);
    }


    @Test
    public void produceHeader() throws Exception{
        Paths.get(".").getFileSystem().getRootDirectories();
        Path ontologyPath = Paths.get(FILE_LOC);
        Header header = new Header(ontologyPath);
        header.write(mockEmitter, mockRequest,mockMediaType);

        //Test
        verify(mockEmitter).send("!gaf-version: 2.1", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.PROJECT_NAME, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.URL, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.EMAIL, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.DATE + "20170126", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.FILTERS_INTRO, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + URI + "?assignedBy=foo,bar", MediaType.TEXT_PLAIN);
    }
}
