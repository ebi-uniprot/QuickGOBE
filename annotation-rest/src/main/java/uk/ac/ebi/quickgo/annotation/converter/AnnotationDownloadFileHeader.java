package uk.ac.ebi.quickgo.annotation.converter;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import uk.ac.ebi.quickgo.annotation.service.http.GAFHttpMessageConverter;
import uk.ac.ebi.quickgo.annotation.service.http.GPADHttpMessageConverter;
import uk.ac.ebi.quickgo.common.loader.GZIPFiles;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Produce a header for downloaded files.
 *
 * Example of a header for a GAF file.
 * !gaf-version: 2.1
 * !Project_name: UniProt GO Annotation (UniProt-GOA)
 * !URL: http://www.ebi.ac.uk/GOA
 * !Contact Email: goa@ebi.ac.uk
 * !Date downloaded from the QuickGO browser: 20170123
 * !Filtering parameters selected to generate file:
 * GAnnotation?count=25&protein=Q4VCS5&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 *
 * Example of a header for a GPAD file:
 * !gpa-version: 1.1
 * !Project_name: UniProt GO Annotation (UniProt-GOA)
 * !URL: http://www.ebi.ac.uk/GOA
 * !Contact Email: goa@ebi.ac.uk
 * !Date downloaded from the QuickGO browser: 20170117
 * !Filtering parameters selected to generate file:
 * GAnnotation?count=25&protein=A0A000&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 *
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 10:09
 * Created with IntelliJ IDEA.
 */
@Component
// todo: move all classes in this package to uk/ac/ebi/quickgo/annotation/service/converter
public class AnnotationDownloadFileHeader {
    Logger logger = LoggerFactory.getLogger(AnnotationDownloadFileHeader.class);

    static final String PROJECT_NAME = "Project_name: UniProt GO Annotation (UniProt-GOA)";
    static final String URL = "URL: http://www.ebi.ac.uk/GOA";
    static final String EMAIL = "Contact Email: goa@ebi.ac.uk";
    static final String DATE = " * !Date downloaded from the QuickGO browser: ";

    static final String FILTERS_INTRO = "Filtering parameters selected to generate file:";
    static final String GAF_VERSION = "gaf-version: 2.1";
    static final String GPAD_VERSION = "gpa-version: 1.1";
    private static final String HEADER_LINE_PREFIX = "!";
    private final Path ontologyPath;
    private List<String> savedOntologyLines;
    private FileTime previousTimeStamp;

    public AnnotationDownloadFileHeader(Path ontologyPath) {
        Preconditions.checkArgument(ontologyPath != null, "The path to the ontology file must not be null");
        this.ontologyPath = ontologyPath;
    }

    /**
     * Write the contents of the header to the ResponseBodyEmitter instance.
     * @param emitter streams the header content to the client
     * @param request holds the URI and parameter list to be added to the header information.
     * @param acceptHeader holds the response type 'GAF' or 'GPAD';
     */
    public void write(ResponseBodyEmitter emitter, HttpServletRequest request, MediaType acceptHeader) {
        send(emitter, version(acceptHeader));
        send(emitter, PROJECT_NAME);
        send(emitter, URL);
        send(emitter, EMAIL);
        send(emitter, date());
        send(emitter, FILTERS_INTRO);
        send(emitter, request(request));
        ontology().forEach(s -> send(emitter, s));

    }

    private void send(ResponseBodyEmitter emitter, String content) {
        try {
            emitter.send(HEADER_LINE_PREFIX + content, MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send download header", e);
        }
    }

    private String version(MediaType acceptHeader) {
        switch (acceptHeader.getSubtype()) {
            case GAFHttpMessageConverter.SUB_TYPE:
                return GAF_VERSION;
            case GPADHttpMessageConverter.SUB_TYPE:
                return GPAD_VERSION;
        }
        throw new IllegalArgumentException("Unknown Media subtype requested: " + acceptHeader.getSubtype());
    }

    private String date() {
        return DATE + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    }

    private String request(HttpServletRequest request) {
        return request.getRequestURI() + "?" + parameterString(request);
    }

    private String parameterString(HttpServletRequest request) {
        return request.getParameterMap()
                      .entrySet()
                      .stream()
                      .map(s -> String.format("%s=%s", s.getKey(), stream(s.getValue())
                              .collect(Collectors.joining(","))))
                      .collect(Collectors.joining("&"));
    }

    private List<String> ontology() {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(ontologyPath);

            if (!lastModifiedTime.equals(previousTimeStamp)) {
                previousTimeStamp = lastModifiedTime;
                savedOntologyLines = GZIPFiles.lines(ontologyPath)
                                              .skip(1)
                                              .map(s -> s.substring(s.indexOf("http:")))
                                              .collect(Collectors.toList());

            }
        } catch (Exception e) {
            savedOntologyLines = Collections.emptyList();
            logger.error("Failed to load the version of the ontology", e);
        }
        return savedOntologyLines;
    }
}
