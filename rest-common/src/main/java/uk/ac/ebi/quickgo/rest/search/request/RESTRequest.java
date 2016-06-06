package uk.ac.ebi.quickgo.rest.search.request;

/**
 * Represents a client request that requires REST communication, and therefore
 * contains variable REST specific configuration details.
 * Created 02/06/16
 * @author Edd
 */
public class RESTRequest implements ClientRequest {
    // fields to populate rest comm fetcher
    @Override public String getSignature() {
        return null;
    }

}
