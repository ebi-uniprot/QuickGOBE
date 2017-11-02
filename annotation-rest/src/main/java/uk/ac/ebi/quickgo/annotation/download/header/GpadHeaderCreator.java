package uk.ac.ebi.quickgo.annotation.download.header;

/**
 * Specific GPAD Header information is contained in this class.
 * @author Tony Wardell
 * Date: 22/05/2017
 * Time: 15:33
 * Created with IntelliJ IDEA.
 */
public class GpadHeaderCreator extends GeneTypeHeaderCreator {

    static final String VERSION = "gpa-version: 1.1";

    public GpadHeaderCreator(OntologyHeaderInfo ontology) {
        super(ontology);
    }

    @Override String version() {
        return VERSION;
    }
}
