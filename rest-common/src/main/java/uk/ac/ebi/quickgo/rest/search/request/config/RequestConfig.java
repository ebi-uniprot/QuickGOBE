package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Defines a data structure that indicates how a {@link ClientRequest} within a table/collection should be processed.
 * </p>
 * Note: this class needs to remain public for spring wiring purposes.
 *
 * @author Ricardo Antunes
 */
public class RequestConfig {
    private static final String COMMA = ",";
    private Set<String> signature;
    private ExecutionType execution;
    private Map<String, String> properties = new HashMap<>();

    public Set<String> getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        Preconditions.checkArgument(signature != null && !signature.trim().isEmpty(),
                "Signature cannot be null or empty");
        this.signature = new HashSet<>(asList(signature.split(COMMA)));
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

        RequestConfig that = (RequestConfig) o;

        if (signature != null ? !signature.equals(that.signature) : that.signature != null) {
            return false;
        }
        if (execution != that.execution) {
            return false;
        }
        return properties != null ? properties.equals(that.properties) : that.properties == null;

    }

    @Override public int hashCode() {
        int result = signature != null ? signature.hashCode() : 0;
        result = 31 * result + (execution != null ? execution.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "RequestConfig{" +
                "signature='" + signature + '\'' +
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