package uk.ac.ebi.quickgo.rest.search.request.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Holds the execution configuration of fields that do not belong to the current data-source.
 * </p>
 * The configuration definitions held within this data structure are used to indicate how to process fields held in
 * collections/tables this RESTful service does not have direct access to.
 * </p>
 * This configuration is read in from a yaml configuration file.
 * @author Ricardo Antunes
 */
@Component
@ConfigurationProperties(prefix = "search.external") class ExternalFilterConfigRetrieval
        implements FilterConfigRetrieval {

    @NestedConfigurationProperty
    private List<FilterConfig> filterConfigs = new ArrayList<>();

    public List<FilterConfig> getFilterConfigs() {
        return filterConfigs;
    }

    public void setFilterConfigs(List<FilterConfig> filterConfigs) {
        if (filterConfigs != null) {
            this.filterConfigs = filterConfigs;
        }
    }

    @Override
    public Optional<FilterConfig> getBySignature(Set<String> signature) {
        return filterConfigs.stream()
                .filter(field -> field.getSignature().equals(signature))
                .findFirst();
    }

    @Override public int hashCode() {
        return filterConfigs.hashCode();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExternalFilterConfigRetrieval that = (ExternalFilterConfigRetrieval) o;

        return filterConfigs.equals(that.filterConfigs);

    }

    @Override public String toString() {
        return "ExternalRequestConfigRetrieval{" +
                "requestConfigs=" + filterConfigs +
                '}';
    }
}