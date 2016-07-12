package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.Aggregate;

/**
 * Converts the domain specific {@link Aggregate} into a data structure SolrJ understands.
 *
 * @author Ricardo Antunes
 */
public interface AggregateConverter<T> {
    /**
     * Converts an {@link Aggregate} into an output format that SolrJ will understand.
     *
     * @param aggregate the aggregate to convert from
     * @return the output format to convert the aggregate to
     */
    T convert(Aggregate aggregate);
}
