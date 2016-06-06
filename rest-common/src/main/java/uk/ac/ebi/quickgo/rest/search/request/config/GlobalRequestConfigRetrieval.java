package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Holds the definitions of both the {@link InternalRequestConfigRetrieval} and the
 * {@link ExternalRequestConfigRetrieval}. It is then capable of providing information for all types of
 * {@link ClientRequest} instance signatures.
 *
 * @author Ricardo Antunes
 */
@Component class GlobalRequestConfigRetrieval implements RequestConfigRetrieval {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternalRequestConfigRetrieval internalExecutionConfig;
    private final ExternalRequestConfigRetrieval externalExecutionConfig;

    @Autowired
    public GlobalRequestConfigRetrieval(
            InternalRequestConfigRetrieval internalExecutionConfig,
            ExternalRequestConfigRetrieval externalExecutionConfig) {
        Preconditions.checkArgument(internalExecutionConfig != null, "InternalExecutionConfiguration cannot be null.");
        Preconditions.checkArgument(externalExecutionConfig != null, "ExternalExecutionConfiguration cannot be null.");

        this.internalExecutionConfig = internalExecutionConfig;
        this.externalExecutionConfig = externalExecutionConfig;
    }

    /**
     * {@inheritDoc}
     *
     * Note: This implementation looks first at the {@link InternalRequestConfigRetrieval} and then at
     * {@link ExternalRequestConfigRetrieval} to find the required field.
     *
     * @param signature the signature of the {@link ClientRequest} whose {@link RequestConfig} one wants
     * @return an Optional containing the correct {@link RequestConfig} or an empty Optional if no config is
     * found for the given signature.
     */
    @Override public Optional<RequestConfig> getSignature(String signature) {
        Preconditions.checkArgument(signature != null && !signature.trim().isEmpty(),
                "Field name cannot be null or empty");

        Optional<RequestConfig> internalConfig = internalExecutionConfig.getSignature(signature);
        Optional<RequestConfig> externalConfig = externalExecutionConfig.getSignature(signature);

        Optional<RequestConfig> config;

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
}