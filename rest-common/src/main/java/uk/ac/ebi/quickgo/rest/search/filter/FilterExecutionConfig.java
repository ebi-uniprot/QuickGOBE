package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Optional;

/**
 * Holds the configuration definitions on how to process {@link RequestFilter} instances.
 *
 * @author Ricardo Antunes
 */
interface FilterExecutionConfig {
    /**
     * Provide the {@link FieldExecutionConfig} for the given {@param fieldName}.
     *
     * @param fieldName the name of the field
     * @return an Optional instance of {@link FieldExecutionConfig} which contains inofrmation on how to process
     * the given field
     * @throws IllegalArgumentException if the field name is null
     */
    Optional<FieldExecutionConfig> getField(String fieldName);
}
