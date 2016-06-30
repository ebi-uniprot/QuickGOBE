package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

/**
 * Defines the {@link FunctionalInterface} contract for transforming a {@link FilterRequest} instance
 * into a corresponding {@link QuickGOQuery}.
 *
 * Created 16/06/16
 * @author Edd
 */
@FunctionalInterface interface FilterConverter {
    QuickGOQuery transform(FilterRequest request);
}
