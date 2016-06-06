package uk.ac.ebi.quickgo.rest.search.request;

import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfigRetrieval;

/**
 * The contract required by a client's query request.
 *
 * Created 02/06/16
 * @author Edd
 */
public interface ClientRequest {
    /**
     * <p>This method produces a unique signature that is
     * associated with this particular type of client request.
     *
     * <p>NB. The purpose of this signature is to allow one to identify
     * possible additional configuration details that are associated
     * with this client request, which are retrieved by {@link RequestConfigRetrieval}
     * @return the unique signature associated with this type of client request
     */
    String getSignature();
}
