package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

/**
 * Contract for building a query request whose use purpose
 * is search specific.
 *
 * Created 11/04/16
 * @author Edd
 */
public interface SearchQueryRequestBuilder {
    QueryRequest build();
}
