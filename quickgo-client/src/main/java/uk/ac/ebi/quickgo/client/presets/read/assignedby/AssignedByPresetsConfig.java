package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.LogStepListener;
import uk.ac.ebi.quickgo.client.presets.read.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.presets.read.ff.*;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.List;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import static java.util.Arrays.asList;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory.Source.DB_COLUMNS;

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
    private static final String ASSIGNED_BY = "assignedBy";
    private static final String ASSIGNED_BY_DEFAULTS = "UniProtKB";

    @Value("#{'${assignedBy.preset.source:}'.split(',')}")
    private Resource[] assignedByResources;
    @Value("${assignedBy.preset.header.lines:1}")
    private int assignedByHeaderLines;
    @Value("#{'${assignedBy.preset.defaults:" + ASSIGNED_BY_DEFAULTS + "}'.split(',')}")
    private String[] assignedByDefaults;

    @Bean
    public Step assignedByStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePreset presets,
            RESTFilterConverterFactory converterFactory) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawAssignedByPresetFieldSetMapper());
        itemReader.setLinesToSkip(assignedByHeaderLines);

        return stepBuilderFactory.get(ASSIGNED_BY_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(assignedByResources, itemReader))
                .processor(compositeItemProcessor(
                        assignedByValidator(),
                        assignedByRelevancyFetcher(converterFactory)))
                .writer(rawItemList -> {
                    rawItemList.forEach(rawItem -> {
                        presets.assignedBy.addPreset(
                                new PresetItem(null, rawItem.name, rawItem.description, rawItem.relevancy));
                    });
                })
                .listener(new LogStepListener())
                .build();
    }

    private FieldSetMapper<RawNamedPreset> rawAssignedByPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(DB_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> assignedByValidator() {
        return new RawNamedPresetValidator();
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> assignedByRelevancyFetcher(
            RESTFilterConverterFactory converterFactory) {
        FilterRequest assignedByRequest = FilterRequest.newBuilder().addProperty(ASSIGNED_BY).build();

        List<String> relevantAssignedByPresets;
        try {
            ConvertedFilter<List<String>> convertedFilter = converterFactory.convert(assignedByRequest);
            relevantAssignedByPresets = convertedFilter.getConvertedValue();
        } catch (Exception e) {
            relevantAssignedByPresets = asList(assignedByDefaults);
        }
        return new RawNamedPresetRelevanceChecker(relevantAssignedByPresets);
    }
}
