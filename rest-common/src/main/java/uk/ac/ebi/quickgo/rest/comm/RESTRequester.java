package uk.ac.ebi.quickgo.rest.comm;

import java.util.concurrent.CompletableFuture;

/**
 * Created 31/05/16
 * @author Edd
 */
public interface RESTRequester {
    <T> CompletableFuture<T> get(Class<T> responseType);
}
