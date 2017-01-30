package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.service.http.GAFHttpMessageConverter;
import uk.ac.ebi.quickgo.annotation.service.http.GPADHttpMessageConverter;

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
public class AnnotationDownloadFileHeaderTest {

    private static final String URI = "/QuickGO/services/annotation/search";
    private static final Map<String, String[]> mockParameterMap = new HashMap<>();
    private static final String todaysDate;
    private static final String ECO_VERSION = "http://purl.obolibrary.org/obo/eco/releases/2017-01-06/eco.owl";
    private static final String GO_VERSION = "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go" +
                                                               ".owl";

    static {
        mockParameterMap.put("assignedBy", new String[]{"foo", "bar"});
        mockParameterMap.put("evidence", new String[]{"ECO:12345"});
        todaysDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
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
        when(mockMediaType.getSubtype()).thenReturn(GAFHttpMessageConverter.SUB_TYPE);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.GAF_VERSION), MediaType.TEXT_PLAIN);
        testRestOfHeader();
    }

    @Test
    public void produceGPADHeader() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn(GPADHttpMessageConverter.SUB_TYPE);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);

        //Test
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.GPAD_VERSION), MediaType.TEXT_PLAIN);
        testRestOfHeader();
    }

    @Test
    public void headerOutputDoesNotContainOntologyInformationWhenFileIsNotAvailable() throws Exception {
        Path ontologyPath = Paths.get("/nowhere/city");
        when(mockMediaType.getSubtype()).thenReturn(GAFHttpMessageConverter.SUB_TYPE);
        annotationDownloadFileHeader = new AnnotationDownloadFileHeader(ontologyPath);
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);
        verify(mockEmitter, never()).send(prefixHeaderLine(ECO_VERSION), MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send(prefixHeaderLine(GO_VERSION), MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send(prefixHeaderLine(""), MediaType.TEXT_PLAIN);
        verify(mockEmitter, never()).send(prefixHeaderLine(""), MediaType.TEXT_PLAIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerOutputThrowsExceptionWhenMediaTypeIsUnexpected() throws Exception {
        when(mockMediaType.getSubtype()).thenReturn("FOOBAR");
        annotationDownloadFileHeader.write(mockEmitter, mockRequest, mockMediaType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathToOntologyFileIsNull() throws Exception {
      new AnnotationDownloadFileHeader(null);
    }

    private void testRestOfHeader() throws IOException {
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.PROJECT_NAME), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.URL), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.EMAIL), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.DATE + todaysDate),
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(AnnotationDownloadFileHeader.FILTERS_INTRO), MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(URI + "?assignedBy=foo,bar&evidence=ECO:12345"),
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(ECO_VERSION),
                                 MediaType.TEXT_PLAIN);
        verify(mockEmitter).send(prefixHeaderLine(GO_VERSION),
                                 MediaType.TEXT_PLAIN);
    }

    private static String prefixHeaderLine(String content) {
        return "!" + content;
    }

}
