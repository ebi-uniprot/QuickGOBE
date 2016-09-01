package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.LogStepListener;
import uk.ac.ebi.quickgo.client.presets.read.PresetsCommonConfig;
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

import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.rawPresetMultiFileReader;

/**
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class AssignedByPresetsConfig {
    private static final String ASSIGNED_BY_LOADING_STEP_NAME = "AssignedByReadingStep";
    private static final String ASSIGNED_BY = "assignedBy";

    @Value("#{'${assignedBy.preset.source:}'.split(',')}")
    private Resource[] assignedByResources;
    @Value("${assignedBy.preset.header.lines:1}")
    private int assignedByHeaderLines;

    @Bean
    public Step assignedByStep(StepBuilderFactory stepBuilderFactory, Integer chunkSize, CompositePreset presets,
            RESTFilterConverterFactory
                    converterFactory) {
        FlatFileItemReader<RawAssignedByPreset> itemReader = fileReader(rawAssignedByPresetFieldSetMapper());
        itemReader.setLinesToSkip(assignedByHeaderLines);

        return stepBuilderFactory.get(ASSIGNED_BY_LOADING_STEP_NAME)
                .<RawAssignedByPreset, RawAssignedByPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawAssignedByPreset>reader(rawPresetMultiFileReader(assignedByResources, itemReader))
                .processor(compositeItemProcessor(
                        assignedByValidator(),
                        assignedByRelevancyFetcher(converterFactory)))
                .writer(list -> list.forEach(rawPreset ->
                        presets.assignedBy
                                .presets
                                .add(new PresetItem(rawPreset.name, rawPreset.description))
                ))
                .listener(new LogStepListener())
                .build();
    }

    private FieldSetMapper<RawAssignedByPreset> rawAssignedByPresetFieldSetMapper() {
        return new StringToAssignedByMapper();
    }

    private ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> assignedByValidator() {
        return new RawAssignedByPresetValidator();
    }

    private ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> assignedByRelevancyFetcher
            (RESTFilterConverterFactory converterFactory) {
        FilterRequest assignedBy = FilterRequest.newBuilder().addProperty(ASSIGNED_BY).build();
        ConvertedFilter<List<String>> convertedFilter = converterFactory.convert(assignedBy);

        return new RawAssignedByPresetTopN(
                assignedByName -> convertedFilter.getConvertedValue().contains(assignedByName));
    }
}
