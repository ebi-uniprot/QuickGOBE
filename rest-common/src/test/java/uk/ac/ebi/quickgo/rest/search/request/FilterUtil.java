package uk.ac.ebi.quickgo.rest.search.request;

import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import java.util.Map;

import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType;

public final class FilterUtil {
    private FilterUtil(){}

    public static RequestConfig createExecutionConfig(String name, ExecutionType type) {
        RequestConfig field = new RequestConfig();
        field.setSignature(name);
        field.setExecution(type);

        return field;
    }

    public static RequestConfig createExecutionConfigWithProps(String name, ExecutionType type,
            Map<String, String> props) {
        RequestConfig field = new RequestConfig();
        field.setSignature(name);
        field.setExecution(type);
        field.setProperties(props);

        return field;
    }
}
