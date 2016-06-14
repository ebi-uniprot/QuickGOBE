package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Map;

import static uk.ac.ebi.quickgo.rest.search.filter.FieldExecutionConfig.*;

public final class FilterUtil {
    private FilterUtil(){}

    public static FieldExecutionConfig createExecutionConfig(String name, ExecutionType type) {
        FieldExecutionConfig field = new FieldExecutionConfig();
        field.setName(name);
        field.setExecution(type);

        return field;
    }

    public static FieldExecutionConfig createExecutionConfigWithProps(String name, ExecutionType type, 
            Map<String, String> props) {
        FieldExecutionConfig field = new FieldExecutionConfig();
        field.setName(name);
        field.setExecution(type);
        field.setProperties(props);

        return field;
    }
}
