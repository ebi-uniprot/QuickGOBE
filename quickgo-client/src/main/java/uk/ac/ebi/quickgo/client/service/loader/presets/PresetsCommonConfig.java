package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

/**
 * Provides common Spring Batch job configuration details and methods used when populating preset information.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.rest"})
public class PresetsCommonConfig {
    @Value("${preset.chunk.size:500}")
    private int chunkSize;

    @Bean
    public Integer chunkSize() {
        return chunkSize;
    }

    @Bean
    public CompositePresetImpl presets() {
        return new CompositePresetImpl();
    }

    @Bean
    public RestValuesRetriever restValuesRetriever(FilterConfigRetrieval externalFilterConfigRetrieval,
                                                   RestOperations restOperations) {
        RESTFilterConverterFactory restConverterFactory =
                new RESTFilterConverterFactory(externalFilterConfigRetrieval, restOperations);
        return new RestValuesRetriever(restConverterFactory);
    }
}
