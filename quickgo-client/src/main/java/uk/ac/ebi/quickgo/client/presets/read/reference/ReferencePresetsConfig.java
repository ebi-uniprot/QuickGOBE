package uk.ac.ebi.quickgo.client.presets.read.reference;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.LogStepListener;
import uk.ac.ebi.quickgo.client.presets.read.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.presets.read.ff.StringToRawNamedPresetMapper;

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
 * Exposes the {@link Step} bean that is used to read and populate information relating to the assignedBy preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class ReferencePresetsConfig {
    private static final String REFERENCE_LOADING_STEP_NAME = "ReferenceGenericDBReadingStep";
    private static final String REFERENCE = "reference";
    private static final String REFERENCE_DEFAULTS = "UniProtKB";

    @Value("#{'${reference.db.preset.source:}'.split(',')}")
    private Resource[] dbResources;
    @Value("${reference.db.preset.header.lines:1}")
    private int dbHeaderLines;
    @Value("#{'${reference.preset.defaults:" + REFERENCE_DEFAULTS + "}'.split(',')}")
    private String[] dbDefaults;

    @Value("#{'${reference.specific.db.preset.source:}'.split(',')}")
    private Resource[] specificResources;
    @Value("${reference.db.preset.header.lines:1}")
    private int specificDBHeaderLines;
    @Value("#{'${reference.preset.defaults:" + REFERENCE_DEFAULTS + "}'.split(',')}")
    private String[] specificDBDefaults;

    @Bean
    public Step referenceGenericDbStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePreset presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawAssignedByPresetFieldSetMapper());
        itemReader.setLinesToSkip(dbHeaderLines);

        return stepBuilderFactory.get(REFERENCE_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(dbResources, itemReader))
                .processor(compositeItemProcessor(
                        assignedByValidator()))
                .writer(rawItemList -> rawItemList.forEach(rawItem -> {
                    presets.references.addPreset(
                            new PresetItem(rawItem.name, rawItem.description, rawItem.relevancy));
                }))
                .listener(new LogStepListener())
                .build();
    }

    // todo: specificReference

    private FieldSetMapper<RawNamedPreset> rawAssignedByPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper();
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> assignedByValidator() {
        return new RawNamedPresetValidator();
    }
}
