package uk.ac.ebi.quickgo.rest.comm;

/**
 * Created 09/08/16
 * @author Edd
 */
public interface ResultTransformer<R> {
    R transform(R result, ConversionContext conversionContext);
}
