package uk.ac.ebi.quickgo.rest.search.request;

import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

public final class FilterUtil {
    private FilterUtil(){}

    public static FilterConfig createExecutionConfig(String name, ExecutionType type) {
        FilterConfig field = new FilterConfig();
        field.setSignature(name);
        field.setExecution(type);

        return field;
    }

    public static FilterConfig createExecutionConfigWithProps(String name, ExecutionType type,
            Map<String, String> props) {
        FilterConfig field = new FilterConfig();
        field.setSignature(name);
        field.setExecution(type);
        field.setProperties(props);

        return field;
    }

    public static <T> Set<T> asSet(T... elements) {
        return new HashSet<>(asList(elements));
    }
}
