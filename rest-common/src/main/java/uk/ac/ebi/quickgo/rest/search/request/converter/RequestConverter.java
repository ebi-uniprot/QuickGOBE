package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

/**
 * Created 16/06/16
 * @author Edd
 */
interface RequestConverter {
    QuickGOQuery transform(ClientRequest request);
}
