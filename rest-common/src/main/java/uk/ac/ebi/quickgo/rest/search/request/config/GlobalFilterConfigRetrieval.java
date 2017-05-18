package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Holds the definitions of both the {@link InternalFilterConfigRetrieval} and the
 * {@link ExternalFilterConfigRetrieval}. It is then capable of providing information for all types of
 * {@link FilterRequest} instance signatures.
 *
 * @author Ricardo Antunes
 */
@Component class GlobalFilterConfigRetrieval implements FilterConfigRetrieval {
    final Map<Set<String>, Optional<FilterConfig>> configCache;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final InternalFilterConfigRetrieval internalExecutionConfig;
    private final ExternalFilterConfigRetrieval externalExecutionConfig;

    @Autowired
    public GlobalFilterConfigRetrieval(
            InternalFilterConfigRetrieval internalExecutionConfig,
            ExternalFilterConfigRetrieval externalExecutionConfig) {
        Preconditions.checkArgument(internalExecutionConfig != null, "InternalExecutionConfiguration cannot be null.");
        Preconditions.checkArgument(externalExecutionConfig != null, "ExternalExecutionConfiguration cannot be null.");

        this.internalExecutionConfig = internalExecutionConfig;
        this.externalExecutionConfig = externalExecutionConfig;
        this.configCache = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * Note: This implementation looks first at the {@link InternalFilterConfigRetrieval} and then at
     * {@link ExternalFilterConfigRetrieval} to find the required field.
     *
     * @param signature the signature of the {@link FilterRequest} whose {@link FilterConfig} one wants
     * @return an Optional containing the correct {@link FilterConfig} or an empty Optional if no config is
     * found for the given signature.
     */
    @Override public Optional<FilterConfig> getBySignature(Set<String> signature) {
        Preconditions.checkArgument(signature != null && !signature.isEmpty(),
                "Signature cannot be null or empty");

        if (configCache.containsKey(signature)) {
            return configCache.get(signature);
        } else {
            Optional<FilterConfig> filterConfig = fetchFilterConfig(signature);
            configCache.put(signature, filterConfig);
            return filterConfig;
        }
    }

    private Optional<FilterConfig> fetchFilterConfig(Set<String> signature) {
        Optional<FilterConfig> internalConfig = internalExecutionConfig.getBySignature(signature);
        Optional<FilterConfig> externalConfig = externalExecutionConfig.getBySignature(signature);

        Optional<FilterConfig> config;

        if(internalConfig.isPresent() && externalConfig.isPresent()) {
            logger.warn(
                    "Both the internal and external execution configurators contain definitions " +
                            "for the signature: {}. Will choose internal config over external config.");
            config = internalConfig;
        } else if(internalConfig.isPresent()) {
            config = internalConfig;
        } else {
            config = externalConfig;
        }

        return config;
    }

    @Override public String toString() {
        return "GlobalFilterConfigRetrieval{" +
                "configCache=" + configCache +
                ", internalExecutionConfig=" + internalExecutionConfig +
                ", externalExecutionConfig=" + externalExecutionConfig +
                '}';
    }
}
