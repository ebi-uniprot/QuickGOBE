package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static uk.ac.ebi.quickgo.rest.search.filter.FieldExecutionConfig.*;

/**
 * This class contains the logic necessary to indicate how a field within the {@link SearchableDocumentFields} is to
 * be searched.
 * </p>
 * Fields declared in {@link SearchableDocumentFields} do not require any external processing configuration to be
 * provided.
 * @author Ricardo Antunes
 */
@Component class InternalFilterExecutionConfig implements FilterExecutionConfig {
    private final Map<String, FieldExecutionConfig> executionConfigs;

    @Autowired
    public InternalFilterExecutionConfig(SearchableDocumentFields searchableDocumentFields) {
        Preconditions
                .checkArgument(searchableDocumentFields != null, "SearchableDocumentFields instance cannot be null.");

        executionConfigs = populateExecutionConfigs(searchableDocumentFields);
    }

    /**
     * {@inheritDoc}
     *
     * If the {@param fieldName} is found to be searchable via {@link  SearchableDocumentFields}, then this method
     * will return a {@link FieldExecutionConfig} instance that indicates that the execution type will be
     * {@link ExecutionType#SIMPLE}.
     *
     * @param fieldName the name of the field
     * @return
     */
    @Override public Optional<FieldExecutionConfig> getField(String fieldName) {
        Preconditions
                .checkArgument(fieldName != null && !fieldName.trim().isEmpty(), "Field name cannot be null or empty");
        return Optional.ofNullable(executionConfigs.get(fieldName));
    }

    private Map<String, FieldExecutionConfig> populateExecutionConfigs(SearchableDocumentFields
            searchableDocumentFields) {
        return searchableDocumentFields.searchableDocumentFields()
                .map(field -> createFieldConfig(field, ExecutionType.SIMPLE))
                .collect(Collectors.toMap(FieldExecutionConfig::getName, Function.identity()));
    }

    private FieldExecutionConfig createFieldConfig(String fieldName, ExecutionType type) {
        FieldExecutionConfig config = new FieldExecutionConfig();
        config.setName(fieldName);
        config.setExecution(type);

        return config;
    }
}