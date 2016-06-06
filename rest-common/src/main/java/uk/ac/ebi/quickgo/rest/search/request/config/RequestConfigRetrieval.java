package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

import java.util.Optional;

/**
 * Holds the configuration definitions on how to process {@link ClientRequest} instances.
 *
 * @author Ricardo Antunes
 */
public interface RequestConfigRetrieval {
    /**
     * Provide the {@link RequestConfig} for the given {@param signature}.
     *
     * @param signature the signature (sorted in alphabetical order, comma-separated) of the
     * {@link ClientRequest}
     * @return an Optional instance of {@link RequestConfig} which contains information on how to process
     * the given request, identified by the signature
     * @throws IllegalArgumentException if the signature is null or empty
     */
    Optional<RequestConfig> getSignature(String signature);
}
