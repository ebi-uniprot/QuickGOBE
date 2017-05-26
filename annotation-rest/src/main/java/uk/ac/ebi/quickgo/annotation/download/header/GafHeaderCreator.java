package uk.ac.ebi.quickgo.annotation.download.header;

/**
 * Specific GAF Header information is contained in this class.
 * @author Tony Wardell
 * Date: 22/05/2017
 * Time: 15:44
 * Created with IntelliJ IDEA.
 */
public class GafHeaderCreator extends GTypeHeaderCreator {

    static final String VERSION = "gaf-version: 2.1";

    public GafHeaderCreator(Ontology ontology) {
        super(ontology);
    }

    @Override String version() {
        return VERSION;
    }
}
