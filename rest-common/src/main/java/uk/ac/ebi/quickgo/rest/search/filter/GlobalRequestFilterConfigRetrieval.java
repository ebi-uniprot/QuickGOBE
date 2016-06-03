package uk.ac.ebi.quickgo.rest.search.filter;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Holds the definitions of both the {@link InternalRequestFilterConfigRetrieval} and the
 * {@link ExternalRequestFilterConfigRetrieval}. It is then capable of providing information for all types of fields.
 *
 * @author Ricardo Antunes
 */
@Component class GlobalRequestFilterConfigRetrieval implements RequestFilterConfigRetrieval {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternalRequestFilterConfigRetrieval internalExecutionConfig;
    private final ExternalRequestFilterConfigRetrieval externalExecutionConfig;

    @Autowired
    public GlobalRequestFilterConfigRetrieval(
            InternalRequestFilterConfigRetrieval internalExecutionConfig,
            ExternalRequestFilterConfigRetrieval externalExecutionConfig) {
        Preconditions.checkArgument(internalExecutionConfig != null, "InternalExecutionConfiguration cannot be null.");
        Preconditions.checkArgument(externalExecutionConfig != null, "ExternalExecutionConfiguration cannot be null.");

        this.internalExecutionConfig = internalExecutionConfig;
        this.externalExecutionConfig = externalExecutionConfig;
    }

    /**
     * {@inheritDoc}
     *
     * Note: This implementation looks first at the {@link InternalRequestFilterConfigRetrieval} and then at
     * {@link ExternalRequestFilterConfigRetrieval} to find the required field.
     *
     * @param signature the name of the field
     * @return an Optional containing the correct {@link RequestFilterConfig} or an empty Optional if no config is
     * found for the given field.
     */
    @Override public Optional<RequestFilterConfig> getSignature(String signature) {
        Preconditions.checkArgument(signature != null && !signature.trim().isEmpty(),
                "Field name cannot be null or empty");

        Optional<RequestFilterConfig> internalConfig = internalExecutionConfig.getSignature(signature);
        Optional<RequestFilterConfig> externalConfig = externalExecutionConfig.getSignature(signature);

        Optional<RequestFilterConfig> config;

        if(internalConfig.isPresent() && externalConfig.isPresent()) {
            logger.warn("Both the internal and external execution configurators contain definitions for the field: {}" +
                    ". Will choose internal config over external config.");
            config = internalConfig;
        } else if(internalConfig.isPresent()) {
            config = internalConfig;
        } else {
            config = externalConfig;
        }

        return config;
    }
}