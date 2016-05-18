package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.filter.FilterFactory;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilter;

import java.util.stream.Stream;

/**
 * Instances of class that implement this method are expected to be able to provide a stream of {@link RequestFilter}.
 *
 * @author Tony Wardell
 * Date: 03/05/2016
 * Time: 10:56
 */
public interface FilterProvider {
    Stream<RequestFilter> convertToFilters(FilterFactory factory);
}
