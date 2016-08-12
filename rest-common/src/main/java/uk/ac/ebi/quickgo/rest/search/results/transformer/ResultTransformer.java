package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.QueryContext;

/**
 * Transforms a specified result of type {@code R} using additional information recorded in a {@link QueryContext}
 * instance.
 *
 * Created 09/08/16
 * @author Edd
 */
@FunctionalInterface
public interface ResultTransformer<R> {
    R transform(R result, QueryContext queryContext);
}
