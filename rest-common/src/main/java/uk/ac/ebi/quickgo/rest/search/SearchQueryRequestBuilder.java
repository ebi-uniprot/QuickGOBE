package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

/**
 * Contract for building a query request whose use purpose
 * is search specific.
 *
 * Created 11/04/16
 * @author Edd
 */
interface SearchQueryRequestBuilder {

    /**
     * Builds a {@link QueryRequest} tailored to searching.
     * @return corresponding {@link QueryRequest} for searching
     */
    QueryRequest build();

    /**
     * Returns a new {@link QueryRequest.Builder} instance
     * to be used to build a new {@link QueryRequest}
     * @return a new {@link QueryRequest.Builder} instance
     */
    QueryRequest.Builder builder();
}
