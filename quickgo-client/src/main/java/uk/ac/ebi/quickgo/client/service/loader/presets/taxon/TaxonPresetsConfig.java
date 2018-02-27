package uk.ac.ebi.quickgo.client.service.loader.presets.taxon;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;

import java.util.concurrent.atomic.AtomicInteger;
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
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.Source.TAXON_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the taxonomy preset data.
 * The required taxonomy information is only the top N taxon IDs. No further information is required.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class TaxonPresetsConfig {
    public static final String TAXON_LOADING_STEP_NAME = "TaxonReadingStep";
    public static final String TAXON_ID = "taxonId";
    private static final AtomicInteger INSERT_ORDER = new AtomicInteger(0);

    @Value("#{'${taxon.preset.source:}'.split(',')}")
    private Resource[] taxonResources;
    @Value("${taxon.preset.header.lines:1}")
    private int taxonHeaderLines;

    @Bean
    public Step taxonStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets) {

        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(fieldSetMapper(TAXON_COLUMNS));
        itemReader.setLinesToSkip(taxonHeaderLines);

        return stepBuilderFactory.get(TAXON_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(
                        rawPresetMultiFileReader(taxonResources, itemReader, PresetsConfigHelper::getGzipResources))
                .processor(compositeItemProcessor(
                        rawPresetValidator()))
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
        return rawItemList -> {
            rawItemList.forEach(rawItem -> {
                presets.addPreset(PresetType.TAXONS,
                        PresetItem
                                .createWithName(rawItem.name)
                                .withProperty(PresetItem.Property.ID.getKey(), rawItem.id)
                                .withRelevancy(INSERT_ORDER.incrementAndGet())
                                .build());
            });
        };
    }

    private FieldSetMapper<RawNamedPreset> fieldSetMapper(SourceColumnsFactory.Source source) {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(source));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetValidator() {
        return new RawNamedPresetValidator();
    }
}
