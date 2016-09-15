package uk.ac.ebi.quickgo.client.presets.read.reference;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItemBuilder;
import uk.ac.ebi.quickgo.client.presets.read.LogStepListener;
import uk.ac.ebi.quickgo.client.presets.read.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.presets.read.ff.StringToRawNamedPresetMapper;

import java.util.Set;
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
import static uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory.Source.DB_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the reference preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class ReferencePresetsConfig {
    public static final String CORE_REFERENCE_DB_LOADING_STEP_NAME = "GenericReferenceDBReadingStep";
    public static final String SPECIFIC_REFERENCE_LOADING_STEP_NAME = "SpecificReferenceReadingStep";
    private static final String REFERENCE_DB_DEFAULTS = "DOI,GO_REF,PMID,REACTOME";
    private static final String REFERENCE_SPECIFIC_DB_DEFAULTS =
            "0000037,0000038,0000043,0000045,0000039,0000040,0000044,0000046,0000003,0000041,0000020,0000002," +
                    "0000042,0000019,0000035,0000049,0000104,0000108";
    private static final RawNamedPreset INVALID_PRESET = null;
    private static final String GO_REF_FORMAT = "GO_REF:%s";

    @Value("#{'${reference.db.preset.source:}'.split(',')}")
    private Resource[] dbResources;
    @Value("${reference.db.preset.header.lines:1}")
    private int dbHeaderLines;
    @Value("#{'${reference.db.preset.defaults:" + REFERENCE_DB_DEFAULTS + "}'.split(',')}")
    private Set<String> dbDefaults;

    @Value("#{'${reference.specific.db.preset.source:}'.split(',')}")
    private Resource[] specificResources;
    @Value("${reference.db.preset.header.lines:1}")
    private int specificDBHeaderLines;
    @Value("#{'${reference.specific.db.preset.defaults:" + REFERENCE_SPECIFIC_DB_DEFAULTS + "}'.split(',')}")
    private Set<String> specificDBDefaults;

    @Bean
    public Step referenceGenericDbStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePreset presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(dbHeaderLines);

        return stepBuilderFactory.get(CORE_REFERENCE_DB_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(dbResources, itemReader))
                .processor(compositeItemProcessor(
                        rawPresetValidator(),
                        rawPresetFilter(dbDefaults)))
                .writer(rawItemList -> rawItemList.forEach(rawItem -> {
                    presets.references.addPreset(
                            PresetItemBuilder.createWithName(rawItem.name)
                                    .withDescription(rawItem.description)
                                    .build());
                }))
                .listener(new LogStepListener())
                .build();
    }

    @Bean
    public Step referenceSpecificDbStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePreset presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(dbHeaderLines);

        return stepBuilderFactory.get(SPECIFIC_REFERENCE_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(specificResources, itemReader))
                .processor(compositeItemProcessor(
                        rawPresetValidator(),
                        rawPresetFilter(specificDBDefaults)))
                .writer(rawItemList -> rawItemList.forEach(rawItem -> {
                    presets.references.addPreset(
                            PresetItemBuilder.createWithName(rawItem.name)
                                    .withDescription(rawItem.description)
                                    .build());
                }))
                .listener(new LogStepListener())
                .build();
    }

    private String buildGORefID(String name) {
        return String.format(GO_REF_FORMAT, name);
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetFilter(Set<String> validPresetNames) {
        return rawNamedPreset ->
                validPresetNames.contains(rawNamedPreset.name) ? rawNamedPreset : INVALID_PRESET;
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(DB_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetValidator() {
        return new RawNamedPresetValidator();
    }
}
