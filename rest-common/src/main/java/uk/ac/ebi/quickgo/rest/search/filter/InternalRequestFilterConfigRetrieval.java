package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Takes care of providing {@link RequestFilterConfig} instances for fields that are searchable via the provided
 * {@link SearchableDocumentFields} instance.
 *
 * @author Ricardo Antunes
 */
@Component
class InternalRequestFilterConfigRetrieval implements RequestFilterConfigRetrieval {
    private final Map<String, RequestFilterConfig> executionConfigs;

    @Autowired
    public InternalRequestFilterConfigRetrieval(SearchableDocumentFields searchableDocumentFields) {
        Preconditions
                .checkArgument(searchableDocumentFields != null, "SearchableDocumentFields instance cannot be null.");

        executionConfigs = populateExecutionConfigs(searchableDocumentFields);
    }

    @Override public Optional<RequestFilterConfig> getSignature(String signature) {
        Preconditions
                .checkArgument(signature != null && !signature.trim().isEmpty(), "Field name cannot be null or empty");
        return Optional.ofNullable(executionConfigs.get(signature));
    }

    private Map<String, RequestFilterConfig> populateExecutionConfigs(SearchableDocumentFields
            searchableDocumentFields) {
        final RequestFilterConfig.ExecutionType executionType = RequestFilterConfig.ExecutionType.SIMPLE;

        return searchableDocumentFields.searchableDocumentFields()
                .map(field -> createFieldConfig(field, executionType))
                .collect(Collectors.toMap(RequestFilterConfig::getSignature, Function.identity()));
    }

    private RequestFilterConfig createFieldConfig(String fieldName, RequestFilterConfig.ExecutionType type) {
        RequestFilterConfig config = new RequestFilterConfig();
        config.setSignature(fieldName);
        config.setExecution(type);

        return config;
    }
}