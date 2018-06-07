package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestOperations;

/**
 * Provides common Spring Batch job configuration details and methods used when populating preset information.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@ComponentScan({"uk.ac.ebi.quickgo.rest"})
public class PresetsCommonConfig {
    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;

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
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RestValuesRetriever restValuesRetriever(FilterConfigRetrieval externalFilterConfigRetrieval,
                                                   RestOperations restOperations) {
        RESTFilterConverterFactory restConverterFactory =
                new RESTFilterConverterFactory(externalFilterConfigRetrieval, restOperations);
        return new RestValuesRetriever(restConverterFactory);
    }
}
