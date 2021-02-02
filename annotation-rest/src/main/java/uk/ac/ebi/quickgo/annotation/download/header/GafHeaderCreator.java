package uk.ac.ebi.quickgo.annotation.download.header;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

/**
 * Specific GAF Header information is contained in this class.
 * @author Tony Wardell
 * Date: 22/05/2017
 * Time: 15:44
 * Created with IntelliJ IDEA.
 */
public class GafHeaderCreator extends GeneTypeHeaderCreator {

    static final String VERSION = "gaf-version: 2.2";
    static final String DATE_GENERATED = "date-generated: ";
    static final String GENERATED_BY = "generated-by: UniProt";

    public GafHeaderCreator(OntologyHeaderInfo ontology) {
        super(ontology);
    }

    @Override String version() {
        return VERSION;
    }

    @Override
    protected void output(ResponseBodyEmitter emitter, HeaderContent content) throws IOException {
        super.output(emitter, content);
        send(emitter, DATE_GENERATED + content.getDate());
        send(emitter, GENERATED_BY);
    }
}
