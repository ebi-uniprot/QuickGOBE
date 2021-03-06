package uk.ac.ebi.quickgo.client.service.loader.presets.withFrom;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.RestValuesRetriever;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;

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

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.compositeItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.ItemProcessorFactory.checkPresetIsUsedItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.ItemProcessorFactory.duplicateCheckingItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.ItemProcessorFactory.fifoRelevancyItemProcessor;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.ItemProcessorFactory.validatingItemProcessor;
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

    public static final String WITH_FROM_DB_LOADING_STEP_NAME = "WithFromDBReadingStep";
    public static final String WITH_FROM_REST_KEY = "withFromPreset";

    @Value("#{'${withfrom.db.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${withfrom.db.preset.header.lines:1}")
    private int headerLines;

    @Bean
    public Step withFromDbStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize, CompositePresetImpl presets, RestValuesRetriever restValuesRetriever) {

        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(WITH_FROM_DB_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT).<RawNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader))
                .processor(compositeItemProcessor(validatingItemProcessor(),
                        checkPresetIsUsedItemProcessor(restValuesRetriever, WITH_FROM_REST_KEY),
                        duplicateCheckingItemProcessor(),
                        fifoRelevancyItemProcessor()))
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
        return rawItemList -> rawItemList.forEach(rawItem ->
                presets.addPreset(PresetType.WITH_FROM,
                        PresetItem.createWithName(rawItem.name)
                                .withProperty(PresetItem.Property.DESCRIPTION.getKey(), rawItem.description)
                                .withRelevancy(rawItem.relevancy)
                                .build())
        );
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return StringToRawNamedPresetMapper.create(SourceColumnsFactory.createFor(DB_COLUMNS));
    }
}
