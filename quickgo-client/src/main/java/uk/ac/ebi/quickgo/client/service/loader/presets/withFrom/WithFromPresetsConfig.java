package uk.ac.ebi.quickgo.client.service.loader.presets.withFrom;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.*;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestOperations;

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.Source.DB_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the with/from preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class WithFromPresetsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WithFromPresetsConfig.class);

    public static final String WITH_FROM_DB_LOADING_STEP_NAME = "WithFromDBReadingStep";
    public static final String WITH_FROM_REST_KEY = "withFrom";

    @Value("#{'${withfrom.db.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${withfrom.db.preset.header.lines:1}")
    private int headerLines;
    private final Set<String> duplicatePrevent = new HashSet<>();

    @Bean
    public Step withFromDbStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets,
            FilterConfigRetrieval externalFilterConfigRetrieval,
            RestOperations restOperations) {

        RESTFilterConverterFactory converterFactory = converterFactory(externalFilterConfigRetrieval,
                restOperations);

        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(WITH_FROM_DB_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader))
                .processor(compositeItemProcessor(
                        new RawNamedPresetValidator(),
                        filterUsingRestResults(converterFactory),
                        duplicateChecker()))
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    ItemProcessor<RawNamedPreset, RawNamedPreset> duplicateChecker() {
        return rawNamedPreset -> duplicatePrevent.add(rawNamedPreset.name.toLowerCase()) ? rawNamedPreset : null;
    }

    private RESTFilterConverterFactory converterFactory(FilterConfigRetrieval filterConfigRetrieval,
            RestOperations restOperations) {
        return new RESTFilterConverterFactory(filterConfigRetrieval, restOperations);
    }

    /**
     * Write the list of {@link RawNamedPreset}s to the {@link CompositePresetImpl}
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    private ItemWriter<RawNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> rawItemList.forEach(rawItem ->
                presets.addPreset(PresetType.WITH_FROM,
                        PresetItem.createWithName(rawItem.name)
                                .withProperty(PresetItem.Property.DESCRIPTION.getKey(), rawItem.description)
                                .withRelevancy(rawItem.relevancy)
                                .build())
        );
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(DB_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> filterUsingRestResults(
            RESTFilterConverterFactory converterFactory) {
        FilterRequest restRequest = FilterRequest.newBuilder().addProperty(WITH_FROM_REST_KEY).build();

        try {
            ConvertedFilter<List<String>> convertedFilter = converterFactory.convert(restRequest);
            final List<String> convertedValues = convertedFilter.getConvertedValue();
            final Set<String> validValues = new HashSet<>(convertedValues);
            return rawNamedPreset -> validValues.contains(rawNamedPreset.name) ? rawNamedPreset : null;
        } catch (RetrievalException | IllegalStateException e) {
            LOGGER.error("Failed to retrieve via REST call the relevant 'with/from' values: ", e);
        }
        return rawNamedPreset -> rawNamedPreset;
    }
}
