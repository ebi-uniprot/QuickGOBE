package uk.ac.ebi.quickgo.annotation.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
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
public class HeaderTest {

    private static final String FILE_LOC = "C:\\Users\\twardell\\IdeaProjects\\QuickGOBE\\annotation-rest\\src\\test" +
            "\\resources/ONTOLOGY_IRI.dat.gz";
    private static final DateFormat YYYYMMDD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String URI = "/QuickGO/services/annotation/search";
    private static final Map<String, String[]> mockParameterMap = new HashMap<>();
    private static final String todaysDate;

    static {
        mockParameterMap.put("assignedBy", new String[]{"foo", "bar"});
        mockParameterMap.put("evidence", new String[]{"ECO:12345"});
        todaysDate = YYYYMMDD_DATE_FORMAT.format(new Date());
    }

    private @Mock ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private @Mock HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    private @Mock MediaType mockMediaType = mock(MediaType.class);
    private Header header;

    @Before
    public void setup() {
        when(mockRequest.getRequestURI()).thenReturn(URI);
        when(mockRequest.getParameterMap()).thenReturn(mockParameterMap);

        Paths.get(".").getFileSystem().getRootDirectories();
        Path ontologyPath = Paths.get(FILE_LOC);
        header = new Header(ontologyPath);
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
