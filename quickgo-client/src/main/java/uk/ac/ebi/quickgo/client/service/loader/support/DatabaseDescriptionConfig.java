package uk.ac.ebi.quickgo.client.service.loader.support;

import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.Source.DB_COLUMNS;

/**
 * @author Tony Wardell
 * Date: 13/03/2018
 * Time: 15:09
 * Created with IntelliJ IDEA.
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class DatabaseDescriptionConfig {
    private static final String DB_DESCRIPTIONS_LOADING_STEP_NAME = "DbDescriptionsLoadingStep";

    @Value("#{'${dbDescriptions.source:}'.split(',')}")
    private Resource[] dbDescriptionsResources;
    @Value("${dbDescriptions.header.lines:1}")
    private int dbDescriptionsHeaderLines;
    private Set<String> duplicatePrevent = new HashSet<>();
    public static Map<String, String> DB_DESCRIPTIONS_MAP = new HashMap<>();

    @Bean
    public Step dbDescriptionsStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize) {

        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(dbDescriptionsMapper());
        itemReader.setLinesToSkip(dbDescriptionsHeaderLines);
        return stepBuilderFactory.get(DB_DESCRIPTIONS_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(dbDescriptionsResources, itemReader))
                .processor(compositeItemProcessor(
                        assignedByValidator(),
                        duplicateChecker()))
                .writer(descriptionsWriter(DB_DESCRIPTIONS_MAP))
                .listener(new LogStepListener())
                .build();
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> assignedByValidator() {
        return new RawNamedPresetValidator();
    }

    private FieldSetMapper<RawNamedPreset> dbDescriptionsMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(DB_COLUMNS));
    }

    ItemProcessor<RawNamedPreset, RawNamedPreset> duplicateChecker() {
        return rawNamedPreset -> duplicatePrevent.add(rawNamedPreset.name.toLowerCase()) ? rawNamedPreset : null;
    }

    private ItemWriter<RawNamedPreset> descriptionsWriter(Map<String, String> descriptionsMap) {
        return rawItemList ->
                rawItemList.forEach(rawItem ->
                        descriptionsMap.put(rawItem.name, rawItem.description));
    }
}
