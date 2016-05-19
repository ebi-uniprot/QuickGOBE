package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

/**
 * Knows how to convert a {@link RequestFilter} into a {@link QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
public interface FilterConverter {

    /**
     * Transforms the filter into a domain specific {@link QuickGOQuery}.
     *
     * Note: It is assumed that if the filter contains multiple values then the resulting QuickGOQuery will have the
     * values implicitly ORed.
     *
     * @return a {@link QuickGOQuery} that represents the filter
     */
    QuickGOQuery transform();
}
