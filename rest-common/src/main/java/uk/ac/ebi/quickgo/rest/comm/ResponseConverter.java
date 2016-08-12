package uk.ac.ebi.quickgo.rest.comm;

/**
 * The {@link FunctionalInterface} contract defining the conversion of a {@link ResponseType} to
 * a {@link ConvertedResponse} of type, {@code T}.
 *
 * Created 09/08/16
 * @author Edd
 */
public interface ResponseConverter<R extends ResponseType, T> {
    ConvertedResponse<T> convert(R response);
}
