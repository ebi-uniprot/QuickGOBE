package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Takes care of providing {@link RequestConfig} instances for {@link uk.ac.ebi.quickgo.rest.search.request.ClientRequest}
 * signatures that are searchable via the provided{@link SearchableDocumentFields} instance.
 *
 * @author Ricardo Antunes
 */
@Component
class InternalRequestConfigRetrieval implements RequestConfigRetrieval {
    private final Map<String, RequestConfig> executionConfigs;

    @Autowired
    public InternalRequestConfigRetrieval(SearchableDocumentFields searchableDocumentFields) {
        Preconditions
                .checkArgument(searchableDocumentFields != null, "SearchableDocumentFields instance cannot be null.");

        executionConfigs = populateExecutionConfigs(searchableDocumentFields);
    }

    @Override public Optional<RequestConfig> getSignature(String signature) {
        Preconditions
                .checkArgument(signature != null && !signature.trim().isEmpty(), "Field name cannot be null or empty");
        return Optional.ofNullable(executionConfigs.get(signature));
    }

    private Map<String, RequestConfig> populateExecutionConfigs(SearchableDocumentFields
            searchableDocumentFields) {
        final RequestConfig.ExecutionType executionType = RequestConfig.ExecutionType.SIMPLE;

        return searchableDocumentFields.searchableDocumentFields()
                .map(field -> createRequestConfig(field, executionType))
                .collect(Collectors.toMap(RequestConfig::getSignature, Function.identity()));
    }

    private RequestConfig createRequestConfig(String signature, RequestConfig.ExecutionType type) {
        RequestConfig config = new RequestConfig();
        config.setSignature(signature);
        config.setExecution(type);

        return config;
    }
}