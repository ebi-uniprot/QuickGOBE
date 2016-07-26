package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;

/**
 * Converts a Solr response into an {@link AggregateResponse} data structure that the domain understands.
 *
 * @author Ricardo Antunes
 */
interface AggregationConverter<T, S extends AggregateResponse> {

    S convert(T response);
}
