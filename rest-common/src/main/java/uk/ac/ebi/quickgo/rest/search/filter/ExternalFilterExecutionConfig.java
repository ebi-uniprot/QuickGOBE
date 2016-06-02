package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Holds the execution configuration of fields that do not belong to the current data-source.
 * </p>
 * The configuration definitions held within this data structure are used to indicate how to process fields held in
 * collections/tables this RESTful service does not have direct access to. It is with these definitions that the
 * {@link FilterConverter} instances will be able to connect to other services in order to execute the field.
 * </p>
 * This configuration is read in from a yaml configuration file.
 * @author Ricardo Antunes
 */
@Component
@ConfigurationProperties(prefix = "search.external")
class ExternalFilterExecutionConfig implements FilterExecutionConfig {

    @NestedConfigurationProperty
    private List<FieldExecutionConfig> fields = new ArrayList<>();

    public List<FieldExecutionConfig> getFields() {
        return fields;
    }

    public void setFields(List<FieldExecutionConfig> fields) {
        if(fields != null) {
            this.fields = fields;
        }
    }

    public Optional<FieldExecutionConfig> getField(String fieldName) {
        return fields.stream()
                .filter(field -> field.getName().equals(fieldName))
                .findFirst();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExternalFilterExecutionConfig that = (ExternalFilterExecutionConfig) o;

        return fields.equals(that.fields);

    }

    @Override public int hashCode() {
        return fields.hashCode();
    }

    @Override public String toString() {
        return "FilterConfigImpl{" +
                "fields=" + fields +
                '}';
    }
}
