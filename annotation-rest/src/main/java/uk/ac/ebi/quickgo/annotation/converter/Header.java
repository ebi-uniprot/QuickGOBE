package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static java.util.Arrays.stream;

/**
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 10:09
 * Created with IntelliJ IDEA.
 *
 * !gaf-version: 2.1
 * !Project_name: UniProt GO Annotation (UniProt-GOA)
 * !URL: http://www.ebi.ac.uk/GOA
 * !Contact Email: goa@ebi.ac.uk
 * !Date downloaded from the QuickGO browser: 20170123
 * !Filtering parameters selected to generate file:
 * GAnnotation?count=25&protein=Q4VCS5&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 *
 * !gpa-version: 1.1
 * !Project_name: UniProt GO Annotation (UniProt-GOA)
 * !URL: http://www.ebi.ac.uk/GOA
 * !Contact Email: goa@ebi.ac.uk
 * !Date downloaded from the QuickGO browser: 20170117
 * !Filtering parameters selected to generate file:
 * GAnnotation?count=25&protein=A0A000&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 */
@Component
public class Header {
    Logger logger = LoggerFactory.getLogger(Header.class);

    static final String PROJECT_NAME = "Project_name: UniProt GO Annotation (UniProt-GOA)";
    static final String URL = "URL: http://www.ebi.ac.uk/GOA";
    static final String EMAIL = "Contact Email: goa@ebi.ac.uk";
    static final String DATE = " * !Date downloaded from the QuickGO browser: ";

    static final String FILTERS_INTRO = "Filtering parameters selected to generate file:";
    static final String GAF_VERSION = "gaf-version: 2.1";
    static final String GPAD_VERSION = "gpa-version: 1.1";
    private static final DateFormat YYYYMMDD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String HEADER_LINE_PREFIX = "!";
    private final Path ontologyPath;
    private List<String> savedOntologyLines;
    private FileTime previousTimeStamp;

    public Header(Path ontologyPath) {
        Preconditions.checkArgument(ontologyPath != null, "The path to the ontology file must not be null");
        this.ontologyPath = ontologyPath;
    }

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
            emitter.send((HEADER_LINE_PREFIX + content), MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send download header", e);
        }
    }

    private String version(MediaType acceptHeader) {
        switch (acceptHeader.getSubtype().toLowerCase()) {
            case "gaf":
                return GAF_VERSION;
            case "gpad":
                return GPAD_VERSION;
        }
        throw new IllegalArgumentException("Unknown Mime Type subtype requested: " + acceptHeader.getSubtype());
    }

    private String date() {
        return DATE + YYYYMMDD_DATE_FORMAT.format(new Date());
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
            FileTime lastModifiedTime = (FileTime)Files.getAttribute(ontologyPath, "lastModifiedTime");

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
