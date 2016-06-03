package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Optional;

/**
 * Holds the configuration definitions on how to process {@link RequestFilterOld} instances.
 *
 * @author Ricardo Antunes
 */
public interface RequestFilterConfigRetrieval {
    /**
     * Provide the {@link RequestFilterConfig} for the given {@param signature}.
     *
     * @param signature the field names (sorted in alphabetical order, comma-separated) of the {@link RequestFilter}
     * @return an Optional instance of {@link RequestFilterConfig} which contains information on how to process
     * the given filter request, identified by the signature
     * @throws IllegalArgumentException if the field name is null or empty
     */
    Optional<RequestFilterConfig> getSignature(String signature);
}
