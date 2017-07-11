package uk.ac.ebi.quickgo.annotation.download.header;

import com.google.common.base.Preconditions;
import java.io.IOException;

import java.util.Objects;
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
public abstract class GTypeHeaderCreator implements HeaderCreator{
    static final String PROJECT_NAME = "Project_name: UniProt GO Annotation (UniProt-GOA)";

    static final String URL = "URL: http://www.ebi.ac.uk/GOA";
    static final String EMAIL = "Contact Email: goa@ebi.ac.uk";
    static final String DATE = "Date downloaded from QuickGO: ";
    static final String FILTERS_INTRO = "Filtering parameters selected to generate file:";

    static final String PREFIX = "!";
    private final Ontology ontology;

    GTypeHeaderCreator(Ontology ontology) {
        Preconditions.checkArgument(ontology != null, "The ontology instance must not be null");
        this.ontology = ontology;
    }

    /**
     * Write the contents of the header to the ResponseBodyEmitter instance.
     * @param emitter streams the header content to the client
     * @param content holds the URI and parameter list to be added to the header information.
     */
    public void write(ResponseBodyEmitter emitter, HeaderContent content) {
        Preconditions.checkArgument(Objects.nonNull(emitter), "The GTypeHeaderCreator emitter must not be null");
        Preconditions.checkArgument(Objects.nonNull(content), "The GTypeHeaderCreator content instance must not be " +
                "null");
        send(emitter, version());
        send(emitter, PROJECT_NAME);
        send(emitter, URL);
        send(emitter, EMAIL);
        send(emitter, date(content));
        ontology.versions().forEach(s -> send(emitter, s));
        send(emitter, FILTERS_INTRO);
        send(emitter, content.getUri());
    }

    private void send(ResponseBodyEmitter emitter, String content) {
        try {
            emitter.send(PREFIX + content + "\n", MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send download header", e);
        }
    }

    abstract String version();

    private String date(HeaderContent content) {
        return DATE + content.getDate();
    }

}
