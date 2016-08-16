package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

/**
 * Converts the domain specific {@link AggregateRequest} into a data structure SolrJ understands.
 *
 * @author Ricardo Antunes
 */
public interface AggregateConverter<T> {
    /**
     * Converts an {@link AggregateRequest} into an output format that SolrJ will understand.
     *
     * @param aggregate the aggregate to convert from
     * @return the output format to convert the aggregate to
     */
    T convert(AggregateRequest aggregate);
}
