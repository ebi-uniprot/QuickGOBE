package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.model.presets.impl.PresetItemBuilder;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;

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
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.Source.GO_SLIM_SET_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the GO slim set preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class GOSlimSetPresetsConfig {
    public static final String GO_SLIM_SET_LOADING_STEP_NAME = "GOSlimSetReadingStep";
    public static final String SLIM = "SLIM";
    private static final RawNamedPreset INVALID_PRESET = null;

    @Value("#{'${go.slimset.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${go.slimset.preset.header.lines:1}")
    private int headerLines;

    @Bean
    public Step goSlimSetStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(GO_SLIM_SET_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader))
                .processor(compositeItemProcessor(
                        rawPresetValidator(),
                        rawPresetFilter()))
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    /**
     * Write the list of {@link RawNamedPreset}s to the {@link CompositePresetImpl}
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    private ItemWriter<RawNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> rawItemList.forEach(rawItem -> {
            presets.goSlimSetsBuilder.addPreset(
                    PresetItemBuilder.createWithName(rawItem.name)
                            .withId(rawItem.id)
                            .build());
        });
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(GO_SLIM_SET_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetFilter() {
        return rawNamedPreset ->
                rawNamedPreset.description.equals(SLIM) ? rawNamedPreset : INVALID_PRESET;
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetValidator() {
        return new RawNamedPresetValidator();
    }
}
