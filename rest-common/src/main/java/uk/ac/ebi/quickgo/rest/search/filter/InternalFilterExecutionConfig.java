package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Takes care of providing {@link FieldExecutionConfig} instances for fields that are searchable via the provided
 * {@link SearchableDocumentFields} instance.
 *
 * @author Ricardo Antunes
 */
@Component
class InternalFilterExecutionConfig implements FilterExecutionConfig {
    private final Map<String, FieldExecutionConfig> executionConfigs;

    @Autowired
    public InternalFilterExecutionConfig(SearchableDocumentFields searchableDocumentFields) {
        Preconditions
                .checkArgument(searchableDocumentFields != null, "SearchableDocumentFields instance cannot be null.");

        executionConfigs = populateExecutionConfigs(searchableDocumentFields);
    }

    @Override public Optional<FieldExecutionConfig> getField(String fieldName) {
        Preconditions
                .checkArgument(fieldName != null && !fieldName.trim().isEmpty(), "Field name cannot be null or empty");
        return Optional.ofNullable(executionConfigs.get(fieldName));
    }

    private Map<String, FieldExecutionConfig> populateExecutionConfigs(SearchableDocumentFields
            searchableDocumentFields) {
        final FieldExecutionConfig.ExecutionType executionType = FieldExecutionConfig.ExecutionType.SIMPLE;

        return searchableDocumentFields.searchableDocumentFields()
                .map(field -> createFieldConfig(field, executionType))
                .collect(Collectors.toMap(FieldExecutionConfig::getName, Function.identity()));
    }

    private FieldExecutionConfig createFieldConfig(String fieldName, FieldExecutionConfig.ExecutionType type) {
        FieldExecutionConfig config = new FieldExecutionConfig();
        config.setName(fieldName);
        config.setExecution(type);

        return config;
    }
}