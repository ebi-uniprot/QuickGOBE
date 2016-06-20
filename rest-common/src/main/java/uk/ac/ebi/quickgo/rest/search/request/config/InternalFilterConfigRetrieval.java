package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Takes care of providing {@link FilterConfig} instances for {@link FilterRequest}
 * signatures that are searchable via the provided{@link SearchableDocumentFields} instance.
 *
 * @author Ricardo Antunes
 */
@Component class InternalFilterConfigRetrieval implements FilterConfigRetrieval {
    private final Map<Set<String>, FilterConfig> executionConfigs;

    @Autowired
    public InternalFilterConfigRetrieval(SearchableDocumentFields searchableDocumentFields) {
        Preconditions
                .checkArgument(searchableDocumentFields != null, "SearchableDocumentFields instance cannot be null.");

        executionConfigs = populateExecutionConfigs(searchableDocumentFields);
    }

    @Override public Optional<FilterConfig> getBySignature(Set<String> signature) {
        Preconditions
                .checkArgument(signature != null && !signature.isEmpty(), "Signature cannot be null or empty");
        return Optional.ofNullable(executionConfigs.get(signature));
    }

    private Map<Set<String>, FilterConfig> populateExecutionConfigs(SearchableDocumentFields
            searchableDocumentFields) {
        final FilterConfig.ExecutionType executionType = FilterConfig.ExecutionType.SIMPLE;

        return searchableDocumentFields.searchableDocumentFields()
                .map(field -> createRequestConfig(field, executionType))
                .collect(Collectors.toMap(FilterConfig::getSignature, Function.identity()));
    }

    private FilterConfig createRequestConfig(String signature, FilterConfig.ExecutionType type) {
        FilterConfig config = new FilterConfig();
        config.setSignature(signature);
        config.setExecution(type);

        return config;
    }
}