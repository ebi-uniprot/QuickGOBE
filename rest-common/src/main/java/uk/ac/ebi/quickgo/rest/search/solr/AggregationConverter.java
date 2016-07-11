package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.results.Aggregation;

/**
 * Converts the response into an {@link Aggregation} data structure that the domain understands.
 *
 * @author Ricardo Antunes
 */
interface AggregationConverter<T, S extends Aggregation> {

    S convert(T response);
}
