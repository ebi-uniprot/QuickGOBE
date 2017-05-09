package uk.ac.ebi.quickgo.annotation.download;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static java.util.Arrays.stream;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GAF_SUB_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GPAD_SUB_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.TSV_SUB_TYPE;

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
public class AnnotationDownloadFileHeader {
    public static final String GO_USAGE_SLIM = "goUsage=slim";
    private static Logger LOGGER = LoggerFactory.getLogger(AnnotationDownloadFileHeader.class);
    static final String PROJECT_NAME = "Project_name: UniProt GO Annotation (UniProt-GOA)";

    static final String URL = "URL: http://www.ebi.ac.uk/GOA";
    static final String EMAIL = "Contact Email: goa@ebi.ac.uk";
    static final String DATE = "Date downloaded from QuickGO: ";
    static final String FILTERS_INTRO = "Filtering parameters selected to generate file:";
    static final String REQUEST_LINE_INDENTATION = "   ";

    static final String GAF_VERSION = "gaf-version: 2.1";
    static final String GPAD_VERSION = "gpa-version: 1.1";
    private static final String HEADER_LINE_PREFIX = "!";
    static final String TSV_COL_HEADINGS_INCLUDING_SLIM = "GENE PRODUCT\tSYMBOL\tQUALIFIER\tGO TERM\tGO TERM " +
            "NAME\tSLIMMED FROM\tEVIDENCE\tREFERENCE\tWITH/FROM\tTAXON\tASSIGNED BY\tANNOTATION EXTENSION" +
            "\tDATE\tTAXON NAME";
    static final String TSV_COL_HEADINGS_EXCLUDING_SLIM = "GENE PRODUCT\tSYMBOL\tQUALIFIER\tGO TERM\tGO TERM " +
            "NAME\tEVIDENCE\tREFERENCE\tWITH/FROM\tTAXON\tASSIGNED BY\tANNOTATION EXTENSION\tDATE\tTAXON NAME";
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
        final String version = version(acceptHeader);
        if(Objects.nonNull(version)){
            send(emitter, version);
        }
        send(emitter, PROJECT_NAME);
        send(emitter, URL);
        send(emitter, EMAIL);
        send(emitter, date());
        ontology().forEach(s -> send(emitter, s));
        send(emitter, FILTERS_INTRO);
        send(emitter, request(request));
        if(TSV_SUB_TYPE.equals(acceptHeader.getSubtype())){
            try {
                emitter.send(colHeadings(request) + "\n", MediaType.TEXT_PLAIN);
            } catch (IOException e) {
                throw new RuntimeException("Failed to send download header", e);
            }
        }
    }

    private String colHeadings(HttpServletRequest request) {
        if(Objects.nonNull(request.getQueryString()) && request.getQueryString().contains(GO_USAGE_SLIM)){
           return TSV_COL_HEADINGS_INCLUDING_SLIM;
        }
        return TSV_COL_HEADINGS_EXCLUDING_SLIM;
    }

    private void send(ResponseBodyEmitter emitter, String content) {
        try {
            emitter.send(HEADER_LINE_PREFIX + content + "\n", MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send download header", e);
        }
    }

    private String version(MediaType acceptHeader) {
        switch (acceptHeader.getSubtype()) {
            case GAF_SUB_TYPE:
                return GAF_VERSION;
            case GPAD_SUB_TYPE:
                return GPAD_VERSION;
            default:
                return null;
        }
    }

    private String date() {
        return DATE + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    }

    private String request(HttpServletRequest request) {
        return REQUEST_LINE_INDENTATION + request.getRequestURI() + "?" + parameterString(request);
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
            LOGGER.error("Failed to load the version of the ontology", e);
        }
        return savedOntologyLines;
    }
}
