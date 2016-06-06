package uk.ac.ebi.quickgo.rest.search.filter;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Holds the definitions of both the {@link InternalFilterExecutionConfig} and the
 * {@link ExternalFilterExecutionConfig}. It is then capable of providing information for all types of fields.
 *
 * @author Ricardo Antunes
 */
@Component class GlobalFilterExecutionConfig implements FilterExecutionConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternalFilterExecutionConfig internalExecutionConfig;
    private final ExternalFilterExecutionConfig externalExecutionConfig;

    @Autowired
    public GlobalFilterExecutionConfig(InternalFilterExecutionConfig internalExecutionConfig,
            ExternalFilterExecutionConfig externalExecutionConfig) {
        Preconditions.checkArgument(internalExecutionConfig != null, "InternalExecutionConfiguration cannot be null.");
        Preconditions.checkArgument(externalExecutionConfig != null, "ExternalExecutionConfiguration cannot be null.");

        this.internalExecutionConfig = internalExecutionConfig;
        this.externalExecutionConfig = externalExecutionConfig;
    }

    /**
     * {@inheritDoc}
     *
     * Note: This implementation looks first at the {@link InternalFilterExecutionConfig} and then at
     * {@link ExternalFilterExecutionConfig} to find the required field.
     *
     * @param fieldName the name of the field
     * @return an Optional containing the correct {@link FieldExecutionConfig} or an empty Optional if no config is
     * found for the given field.
     */
    @Override public Optional<FieldExecutionConfig> getField(String fieldName) {
    @Override public Optional<FieldExecutionConfig> getConfig(String fieldName) {
        Preconditions.checkArgument(fieldName != null && !fieldName.trim().isEmpty(),
                "Field name cannot be null or empty");

        Optional<FieldExecutionConfig> internalConfig = internalExecutionConfig.getField(fieldName);
        Optional<FieldExecutionConfig> externalConfig = externalExecutionConfig.getField(fieldName);
        Optional<FieldExecutionConfig> config = internalExecutionConfig.getConfig(fieldName);

        Optional<FieldExecutionConfig> config;

        if(internalConfig.isPresent() && externalConfig.isPresent()) {
            logger.warn("Both the internal and external execution configurators contain definitions for the field: {}" +
                    ". Will choose internal config over external config.");
            config = internalConfig;
        } else if(internalConfig.isPresent()) {
            config = internalConfig;
        } else {
            config = externalConfig;
        if (!config.isPresent()) {
            config = externalExecutionConfig.getConfig(fieldName);
        }

        return config;
    }
}
