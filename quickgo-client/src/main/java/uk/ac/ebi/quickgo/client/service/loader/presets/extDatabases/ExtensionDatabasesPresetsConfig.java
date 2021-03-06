package uk.ac.ebi.quickgo.client.service.loader.presets.extDatabases;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.ItemProcessorFactory.validatingItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.Source.EXT_DATABASE_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the annotation extension
 * databases preset data.
 * <p>
 * Created 21/03/16
 *
 * @author TonyW
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class ExtensionDatabasesPresetsConfig {
    public static final String EXTENSION_DB_LOADING_STEP_NAME = "ExtensionDatabasesReadingStep";

    @Value("#{'${extdatabase.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${extdatabase.preset.header.lines:1}")
    private int headerLines;

    @Bean
    public Step extensionDatabasesProductStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(EXTENSION_DB_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(
                        rawPresetMultiFileReader(resources, itemReader))
                .processor(compositeRawPresetProcessor())
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    /**
     * Write the list of {@link RawNamedPreset}s to the {@link CompositePresetImpl}
     *
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    private ItemWriter<RawNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> rawItemList.forEach(rawItem -> {
                presets.addPreset(PresetType.EXT_DATABASES,
                                  PresetItem.createWithName(rawItem.name)
                                            .build());
        });
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return StringToRawNamedPresetMapper.create(SourceColumnsFactory.createFor(EXT_DATABASE_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> compositeRawPresetProcessor() {
        CompositeItemProcessor<RawNamedPreset,RawNamedPreset> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(Arrays.asList(validatingItemProcessor(), rawPresetUniqueifier()));
        return compositeItemProcessor;
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetUniqueifier() {
        return new DatabaseFilterItemProcessor();
    }

    /**
     * This implementation assumes that there is enough room in memory to store the duplicate databases.
     * Otherwise, you'd want to store them somewhere you can do a look-up on.
     */
    private class DatabaseFilterItemProcessor implements ItemProcessor<RawNamedPreset, RawNamedPreset> {

        private Set<String> seenDatabases = new HashSet<>();

        public RawNamedPreset process(RawNamedPreset rawNamedPreset) {
            if(seenDatabases.add(rawNamedPreset.name)) {
                return rawNamedPreset;
            } else {
                return null;
            }
        }
    }
}
