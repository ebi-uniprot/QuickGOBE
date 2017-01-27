package uk.ac.ebi.quickgo.annotation.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 17:00
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HeaderTestConfig.class)
public class HeaderTest {

    private static final String URI = "/QuickGO/services/annotation/search";
    private static final Map<String, String[]> mockParameterMap = new HashMap<>();
    private static final String todaysDate;

    static {
        mockParameterMap.put("assignedBy", new String[]{"foo", "bar"});
        mockParameterMap.put("evidence", new String[]{"ECO:12345"});
        todaysDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    }

    private @Mock ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private @Mock HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    private @Mock MediaType mockMediaType = mock(MediaType.class);

    @Autowired
    private Header header;


    @Before
    public void setup() throws IOException {
        when(mockRequest.getRequestURI()).thenReturn(URI);
        when(mockRequest.getParameterMap()).thenReturn(mockParameterMap);
    }

    @Test
    public void produceGAFHeader() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn("GAF");
        header.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        verify(mockEmitter).send("!" + Header.GAF_VERSION, MediaType.TEXT_PLAIN);
        testRestOfHeader();
    }

    @Test
    public void produceGPADHeader() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn("GPAD");
        header.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        verify(mockEmitter).send("!" + Header.GPAD_VERSION, MediaType.TEXT_PLAIN);
        testRestOfHeader();
    }

    @Test
    public void headerOutputDoesNotContainOntologyInformationWhenFileIsNotAvailable() throws Exception {
        Path ontologyPath = Paths.get("/nowhere/city");
        when(mockMediaType.getSubtype()).thenReturn("GAF");
        header = new Header(ontologyPath);
        header.write(mockEmitter, mockRequest, mockMediaType);
        verify(mockEmitter, never()).send("!" + "http://purl.obolibrary.org/obo/eco/releases/2017-01-06/eco.owl",
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send("!" + "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go.owl",
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send("!", MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send("!", MediaType.TEXT_PLAIN);
    }


    @Test(expected = IllegalArgumentException.class)
    public void headerOutputThrowsExceptionWhenMediaTypeIsUnexpected() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn("FOOBAR");
        header.write(mockEmitter, mockRequest, mockMediaType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathToOntologyFileIsNull() throws Exception {
      new Header(null);
    }

    private void testRestOfHeader() throws IOException {
        verify(mockEmitter).send("!" + Header.PROJECT_NAME, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.URL, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.EMAIL, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.DATE + todaysDate, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + Header.FILTERS_INTRO, MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + URI + "?assignedBy=foo,bar&evidence=ECO:12345", MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + "http://purl.obolibrary.org/obo/eco/releases/2017-01-06/eco.owl",
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter).send("!" + "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go.owl",
                                 MediaType.TEXT_PLAIN);
    }
}
