package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Takes care of providing {@link FilterConfig} instances for {@link FilterRequest}
 * signatures that are searchable via the provided{@link SearchableField} instance.
 *
 * @author Ricardo Antunes
 */
@Component class InternalFilterConfigRetrieval implements FilterConfigRetrieval {
    private final Map<Set<String>, FilterConfig> executionConfigs;

    public InternalFilterConfigRetrieval(SearchableField searchableField) {
        Preconditions
                .checkArgument(searchableField != null, "SearchableField instance cannot be null.");

        executionConfigs = populateExecutionConfigs(searchableField);
    }

    @Override public Optional<FilterConfig> getBySignature(Set<String> signature) {
        Preconditions
                .checkArgument(signature != null && !signature.isEmpty(), "Signature cannot be null or empty");
        return Optional.ofNullable(executionConfigs.get(signature));
    }

    private Map<Set<String>, FilterConfig> populateExecutionConfigs(SearchableField searchableField) {
        final FilterConfig.ExecutionType executionType = FilterConfig.ExecutionType.SIMPLE;

        return searchableField.searchableFields()
                .map(field -> createRequestConfig(field, executionType))
                .collect(Collectors.toMap(FilterConfig::getSignature, Function.identity()));
    }

    private FilterConfig createRequestConfig(String signature, FilterConfig.ExecutionType type) {
        FilterConfig config = new FilterConfig();
        config.setSignature(signature);
        config.setExecution(type);

        return config;
    }

    @Override public String toString() {
        return "InternalFilterConfigRetrieval{" +
                "executionConfigs=" + executionConfigs +
                '}';
    }
}
