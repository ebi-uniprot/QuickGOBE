package uk.ac.ebi.quickgo.annotation.download.header;

import com.google.common.base.Preconditions;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * Produce a header for GPAD and GAF files.
 *
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 10:09
 * Created with IntelliJ IDEA.
 */
public abstract class GeneTypeHeaderCreator extends AbstractHeaderCreator {
    static final String PROJECT_NAME = "Project_name: EBI GO Annotation program (GOA)";
    static final String URL = "URL: https://www.ebi.ac.uk/GOA";
    static final String ANNOTATION_URL = "https://www.ebi.ac.uk/QuickGO/annotations";
    static final String EMAIL = "Contact Email: goa@ebi.ac.uk";
    static final String DATE = "Date downloaded from QuickGO: ";
    static final String FILTERS_INTRO = "Filtering parameters selected to generate file:";
    static final String PREFIX = "!";

    private final OntologyHeaderInfo ontology;

    GeneTypeHeaderCreator(OntologyHeaderInfo ontology) {
        Preconditions.checkArgument(ontology != null, "The ontology instance must not be null");
        this.ontology = ontology;
    }

    /**
     * Write the contents of the header to the ResponseBodyEmitter instance.
     * @param emitter streams the header content to the client
     * @param content holds the URI and parameter list to be added to the header information.
     */
    @Override
    protected void output(ResponseBodyEmitter emitter, HeaderContent content) throws IOException {
        send(emitter, version());
        send(emitter, PROJECT_NAME);
        send(emitter, URL);
        send(emitter, EMAIL);
        send(emitter, DATE + content.getDate());
        for (String s : ontology.versions()) {
            send(emitter, s);
        }
        send(emitter, FILTERS_INTRO);
        send(emitter, ANNOTATION_URL + getURLParams(content.getUri()));
    }

    abstract String version();

    void send(ResponseBodyEmitter emitter, String content) throws IOException {
        emitter.send(PREFIX + content + "\n", MediaType.TEXT_PLAIN);
    }

    private String getURLParams(String uri){
        uri = uri == null ? "" : uri.trim();
        final int index = uri.indexOf("?");
        return index < 0 ? "" : uri.substring(index, uri.length());
    }
}
