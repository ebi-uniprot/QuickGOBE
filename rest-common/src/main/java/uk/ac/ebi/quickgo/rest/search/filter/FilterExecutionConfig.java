package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Optional;

/**
 * Holds the configuration definitions on how to process {@link RequestFilterOld} instances.
 *
 * @author Ricardo Antunes
 */
// todo: rename RequestFilterExecutionConfigRetrieval
interface FilterExecutionConfig {
    /**
     * Provide the {@link FieldExecutionConfig} for the given {@param fieldName}.
     *
     * @param fieldName the name of the field
     * @return an Optional instance of {@link FieldExecutionConfig} which contains information on how to process
     * the given field
     * @throws IllegalArgumentException if the field name is null or empty
     */
    // todo should be searching for signature of request
    Optional<FieldExecutionConfig> getField(String fieldName);
}
