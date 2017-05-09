package uk.ac.ebi.quickgo.annotation.download;

import uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory;

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
import static uk.ac.ebi.quickgo.annotation.download.AnnotationDownloadFileHeader.GO_USAGE_SLIM;
import static uk.ac.ebi.quickgo.annotation.download.AnnotationDownloadFileHeader.REQUEST_LINE_INDENTATION;
import static uk.ac.ebi.quickgo.annotation.download.AnnotationDownloadFileHeader.TSV_COL_HEADINGS_EXCLUDING_SLIM;
import static uk.ac.ebi.quickgo.annotation.download.AnnotationDownloadFileHeader.TSV_COL_HEADINGS_INCLUDING_SLIM;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GAF_SUB_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GPAD_SUB_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.TSV_SUB_TYPE;

/**
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 17:00
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnnotationDownloadFileHeaderTestConfig.class)
public class AnnotationDownloadFileHeaderTest {

    private static final String URI = "/QuickGO/services/annotation/search";
    private static final Map<String, String[]> mockParameterMap = new HashMap<>();
    private static final String TODAYS_DATE;
    private static final String ECO_VERSION = "http://purl.obolibrary.org/obo/eco/releases/2017-01-06/eco.owl";
    private static final String GO_VERSION = "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go" +
            ".owl";

    static {
        mockParameterMap.put("assignedBy", new String[]{"foo", "bar"});
        mockParameterMap.put("evidence", new String[]{"ECO:12345"});
        TODAYS_DATE = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    }

    private @Mock ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private @Mock HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    private @Mock MediaType mockMediaType = mock(MediaType.class);

    @Autowired
    private AnnotationDownloadFileHeader annotationDownloadFileHeader;

    @Before
    public void setup() throws IOException {
        when(mockRequest.getRequestURI()).thenReturn(URI);
        when(mockRequest.getParameterMap()).thenReturn(mockParameterMap);
    }

    @Test
    public void produceGAFHeader() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn(GAF_SUB_TYPE);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.GAF_VERSION), MediaType.TEXT_PLAIN);
        testMostOfHeader();
    }

    @Test
    public void produceGPADHeader() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn(GPAD_SUB_TYPE);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.GPAD_VERSION), MediaType.TEXT_PLAIN);
        testMostOfHeader();
    }

    @Test
    public void produceTSVHeader() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn(TSV_SUB_TYPE);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        testMostOfHeader();
        verify(mockEmitter).send(TSV_COL_HEADINGS_EXCLUDING_SLIM + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    public void produceTSVHeaderForSlimmedRequest() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn(TSV_SUB_TYPE);
        when(mockRequest.getQueryString()).thenReturn(GO_USAGE_SLIM);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        testMostOfHeader();
        verify(mockEmitter).send(TSV_COL_HEADINGS_INCLUDING_SLIM + "\n", MediaType.TEXT_PLAIN);
    }

    @Test
    public void headerOutputDoesNotContainOntologyInformationWhenFileIsNotAvailable() throws Exception {
        Path ontologyPath = Paths.get("/nowhere/city");
        when(mockMediaType.getSubtype()).thenReturn(GAF_SUB_TYPE);
        annotationDownloadFileHeader = new AnnotationDownloadFileHeader(ontologyPath);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);
        verify(mockEmitter, never()).send(decorateContent(ECO_VERSION), MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send(decorateContent(GO_VERSION), MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send(decorateContent(""), MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send(decorateContent(""), MediaType.TEXT_PLAIN);
    }

    @Test
    public void headerOutputIsDefaultWhenMediaTypeIsUnexpected() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn("FOOBAR");
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        testMostOfHeader();
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathToOntologyFileIsNull() throws Exception {
        new AnnotationDownloadFileHeader(null);
    }

    private void testMostOfHeader() throws IOException {
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.PROJECT_NAME), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.URL), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.EMAIL), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.DATE + TODAYS_DATE),
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(AnnotationDownloadFileHeader.FILTERS_INTRO), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(REQUEST_LINE_INDENTATION + URI + "?assignedBy=foo,bar&evidence=ECO:12345"),
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(ECO_VERSION),
                MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(decorateContent(GO_VERSION),
                MediaType.TEXT_PLAIN);
    }

    private static String decorateContent(String content) {
        return "!" + content + "\n";
    }

}
