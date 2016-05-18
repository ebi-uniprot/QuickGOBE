package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.stream.Stream;

/**
 * Represents a filter at the web application level.
 *
 * When a client communicates that it wants the search query to be filtered by a given parameter, the REST controller
 * will receive those parameters and transform them into RequestFilters.
 *
 * A request filter can then be transformed into a {@link QuickGOQuery}, which is then understandable by the data
 * source executing the filter.
 *
 * @author Ricardo Antunes.
 */
public interface RequestFilter {
    /**
     * Provides the field (column) the filter will be applied to.
     *
     * @return the field
     */
    String getField();

    /**
     * Provides a {@link Stream} of filtering values pertaining to the {@link #getField()} field.
     *
     * @return the filtering values
     */
    Stream<String> getValues();

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
