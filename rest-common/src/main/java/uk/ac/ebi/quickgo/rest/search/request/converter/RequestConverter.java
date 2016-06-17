package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

/**
 * Defines the {@link FunctionalInterface} contract for transforming a {@link ClientRequest} instance
 * into a corresponding {@link QuickGOQuery}.
 *
 * Created 16/06/16
 * @author Edd
 */
@FunctionalInterface
interface RequestConverter {
    QuickGOQuery transform(ClientRequest request);
}
