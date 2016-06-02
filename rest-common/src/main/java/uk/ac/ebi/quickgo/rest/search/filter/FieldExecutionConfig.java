package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a data structure that indicates how a field within a table/collection should be processed.
 * </p>
 * Note: this class needs to remain public for spring wiring purposes.
 *
 * @author Ricardo Antunes
 */
public class FieldExecutionConfig {
    private String name;
    private ExecutionType execution;
    private Map<String, String> properties = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExecutionType getExecution() {
        return execution;
    }

    public void setExecution(ExecutionType execution) {
        this.execution = execution;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        if (properties != null) {
            this.properties = properties;
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldExecutionConfig field = (FieldExecutionConfig) o;

        return name.equals(field.name);
    }

    @Override public int hashCode() {
        return name.hashCode();
    }

    @Override public String toString() {
        return "FieldExecutionConfig{" +
                "name='" + name + '\'' +
                ", execution=" + execution +
                ", properties=" + properties +
                '}';
    }

    /**
     * Specifies the execution types that can be carried out by a {@link FilterConverter} instance.
     *
     * The enum values are:
     * <ul>
     *     <ui>{@link ExecutionType#SIMPLE} - A filter that can be applied directly on the data source</ui>
     *     <ui>{@link ExecutionType#JOIN} - A filter that requires join two data source tables/collections</ui>
     *     <ui>{@link ExecutionType#REST_COMM} - The filter requires that information be retrieved from another
     *     service. So a REST request will need to be sent.</ui>
     * </ul>
     */
    public enum ExecutionType {
        SIMPLE,
        JOIN,
        REST_COMM
    }
}