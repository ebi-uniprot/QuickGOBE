package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

import java.util.Optional;
import java.util.Set;

/**
 * Holds the configuration definitions on how to process {@link ClientRequest} instances.
 *
 * @author Ricardo Antunes
 */
public interface RequestConfigRetrieval {
    /**
     * Provide the {@link RequestConfig} for the given {@param signature}.
     *
     * @param signature the signature of the {@link ClientRequest}, whose {@link RequestConfig}
     *        needs retrieving. The signature value is a set of {@link String}s, whose items are
     *        the field names associated with the request. Since a request might involve multiple fields,
     *        signatures are of the type {@link Set}.
     * @return an Optional instance of {@link RequestConfig} which contains information on how to process
     * the given request, which was identified by the signature
     * @throws IllegalArgumentException if the signature is null or empty
     */
    Optional<RequestConfig> getBySignature(Set<String> signature);
}
