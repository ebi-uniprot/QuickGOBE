package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.model.presets.slimsets.PresetSlimSetItem;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import static java.util.Collections.singletonList;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;

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

    @Value("#{'${go.slimset.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${go.slimset.preset.header.lines:1}")
    private int headerLines;

    @Bean
    public Step goSlimSetStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets) {
        FlatFileItemReader<RawSlimSetNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(GO_SLIM_SET_LOADING_STEP_NAME)
                .<RawSlimSetNamedPreset, RawSlimSetNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawSlimSetNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader))
                .processor(rawPresetValidator())
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    /**
     * Write the list of {@link RawSlimSetNamedPreset}s to the {@link CompositePresetImpl}
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    private ItemWriter<RawSlimSetNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> rawItemList.forEach(rawItem ->
                presets.addPreset(PresetType.GO_SLIMS_SETS,
                        PresetItem.createWithName(rawItem.name)
                                .withProperty(PresetItem.Property.ID.getKey(), rawItem.id)
                                .withProperty(PresetItem.Property.DESCRIPTION.getKey(), rawItem.description)
                                .withAssociations(singletonList(PresetItem.createWithName(rawItem.association).build()))
                                .withProperty(PresetSlimSetItem.Property.ROLE.getKey(), rawItem.role)
                                .withProperty(PresetSlimSetItem.Property.TAX_IDS.getKey(), rawItem.taxIds)
                                .withProperty(PresetSlimSetItem.Property.SHORT_LABEL.getKey(), rawItem.shortLabel)
                                .build())
        );
    }

    private FieldSetMapper<RawSlimSetNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawSlimSetNamedPresetMapper(SourceColumnsFactory.createSlimSetColumns());
    }

    private ItemProcessor<RawSlimSetNamedPreset, RawSlimSetNamedPreset> rawPresetValidator() {
        return new RawSlimSetNamedPresetValidator();
    }
}
