package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Map;

import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType;

public final class FilterUtil {
    private FilterUtil(){}

    public static RequestFilterConfig createExecutionConfig(String name, ExecutionType type) {
        RequestFilterConfig field = new RequestFilterConfig();
        field.setSignature(name);
        field.setExecution(type);

        return field;
    }

    public static RequestFilterConfig createExecutionConfigWithProps(String name, ExecutionType type,
            Map<String, String> props) {
        RequestFilterConfig field = new RequestFilterConfig();
        field.setSignature(name);
        field.setExecution(type);
        field.setProperties(props);

        return field;
    }
}
