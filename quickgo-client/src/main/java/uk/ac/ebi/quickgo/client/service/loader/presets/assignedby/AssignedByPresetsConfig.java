package uk.ac.ebi.quickgo.client.service.loader.presets.assignedby;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
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

import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.Source.DB_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the assignedBy preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class AssignedByPresetsConfig {
    public static final String ASSIGNED_BY_LOADING_STEP_NAME = "AssignedByReadingStep";
    public static final String ASSIGNED_BY = "assignedBy";

    private static final Logger LOGGER = getLogger(AssignedByPresetsConfig.class);

    @Value("#{'${assignedBy.preset.source:}'.split(',')}")
    private Resource[] assignedByResources;
    @Value("${assignedBy.preset.header.lines:1}")
    private int assignedByHeaderLines;
    private Set<String> duplicatePrevent = new HashSet<>();

    @Bean
    public Step assignedByStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets,
            FilterConfigRetrieval externalFilterConfigRetrieval,
            RestOperations restOperations) {

        RESTFilterConverterFactory converterFactory = assignedByConverterFactory(externalFilterConfigRetrieval,
                restOperations);

        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawAssignedByPresetFieldSetMapper());
        itemReader.setLinesToSkip(assignedByHeaderLines);
        return stepBuilderFactory.get(ASSIGNED_BY_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(assignedByResources, itemReader))
                .processor(compositeItemProcessor(
                        assignedByValidator(),
                        assignedByFilter(converterFactory),
                        duplicateChecker()))
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    ItemProcessor<RawNamedPreset, RawNamedPreset> duplicateChecker() {
        return rawNamedPreset -> duplicatePrevent.add(rawNamedPreset.name.toLowerCase()) ? rawNamedPreset : null;
    }

    private RESTFilterConverterFactory assignedByConverterFactory(FilterConfigRetrieval filterConfigRetrieval,
            RestOperations restOperations) {
        return new RESTFilterConverterFactory(filterConfigRetrieval, restOperations);
    }

    /**
     * Write the list of {@link RawNamedPreset}s to the {@link CompositePresetImpl}
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    private ItemWriter<RawNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList ->
                rawItemList.forEach(rawItem ->
                        presets.addPreset(PresetType.ASSIGNED_BY,
                                PresetItem.createWithName(rawItem.name)
                                        .withProperty(PresetItem.Property.DESCRIPTION.getKey(), rawItem.description)
                                        .withRelevancy(rawItem.relevancy)
                                        .build())
                );
    }

    private FieldSetMapper<RawNamedPreset> rawAssignedByPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(DB_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> assignedByValidator() {
        return new RawNamedPresetValidator();
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> assignedByFilter(
            RESTFilterConverterFactory converterFactory) {
        FilterRequest assignedByRequest = FilterRequest.newBuilder().addProperty(ASSIGNED_BY).build();

        try {
            ConvertedFilter<List<String>> convertedFilter = converterFactory.convert(assignedByRequest);
            final List<String> convertedValues = convertedFilter.getConvertedValue();
            final Set<String> validValues = new HashSet<>(convertedValues);
            return rawNamedPreset -> validValues.contains(rawNamedPreset.name) ? rawNamedPreset : null;
        } catch (RetrievalException | IllegalStateException e) {
            LOGGER.error("Failed to retrieve via REST call the relevant 'assignedBy' values: ", e);
        }
        return rawNamedPreset -> rawNamedPreset;
    }
}
