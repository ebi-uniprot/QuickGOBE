package uk.ac.ebi.quickgo.annotation.download.header;

/**
 * @author Tony Wardell
 * Date: 22/05/2017
 * Time: 15:44
 * Created with IntelliJ IDEA.
 */
public class GafHeaderCreator extends GTypeHeaderCreator {

    public static final String VERSION = "gaf-version: 2.1";

    public GafHeaderCreator(Ontology ontology) {
        super(ontology);
    }

    @Override String version() {
        return VERSION;
    }
}
