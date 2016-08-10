package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.comm.ConversionContext;

/**
 * Created 09/08/16
 * @author Edd
 */
@FunctionalInterface
public interface ResultTransformer<R> {
    R transform(R result, ConversionContext conversionContext);
}
