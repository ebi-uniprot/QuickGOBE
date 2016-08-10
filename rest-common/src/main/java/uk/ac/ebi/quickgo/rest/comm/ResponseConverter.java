package uk.ac.ebi.quickgo.rest.comm;

/**
 * Created 09/08/16
 * @author Edd
 */
public interface ResponseConverter<R extends ResponseType, T> {
    ConvertedResponse<T> convert(R response);
}
