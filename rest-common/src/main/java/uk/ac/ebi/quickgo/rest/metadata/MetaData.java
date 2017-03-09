package uk.ac.ebi.quickgo.rest.metadata;

/**
 * Simple data structure for a service's meta data.
 *
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 11:03
 * Created with IntelliJ IDEA.
 */
public class MetaData {

    public String version;
    public String timestamp;

    public MetaData(String version, String timestamp) {
        this.version = version;
        this.timestamp = timestamp;
    }
}
