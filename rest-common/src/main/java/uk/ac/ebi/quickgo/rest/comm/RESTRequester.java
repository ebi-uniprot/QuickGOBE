package uk.ac.ebi.quickgo.rest.comm;

import java.util.concurrent.CompletableFuture;

/**
 * <p/>Contract for making REST requests and retrieving a response
 * of a specified type.
 *
 * Created 31/05/16
 * @author Edd
 */
@FunctionalInterface interface RESTRequester {
    <T> CompletableFuture<T> get(Class<T> responseType);
}
