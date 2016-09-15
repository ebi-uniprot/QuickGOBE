package uk.ac.ebi.quickgo.client.presets.read.geneproduct;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItemBuilder;
import uk.ac.ebi.quickgo.client.presets.read.LogStepListener;
import uk.ac.ebi.quickgo.client.presets.read.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.presets.read.ff.RawNamedPresetValidator;
import uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory;
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
import static uk.ac.ebi.quickgo.client.presets.read.ff.SourceColumnsFactory.Source.GENE_PRODUCT_COLUMNS;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the gene product preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class GeneProductPresetsConfig {
    public static final String GENE_PRODUCT_LOADING_STEP_NAME = "GeneProductReadingStep";

    @Value("#{'${geneproduct.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${geneproduct.preset.header.lines:1}")
    private int headerLines;

    @Bean
    public Step geneProductStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePreset presets) {
        FlatFileItemReader<RawNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(GENE_PRODUCT_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader))
                .processor(compositeItemProcessor(
                        rawPresetValidator()))
                .writer(rawItemList -> rawItemList.forEach(rawItem -> {
                    presets.geneProducts.addPreset(
                            PresetItemBuilder.createWithName(rawItem.name)
                                    .withDescription(rawItem.description)
                                    .withUrl(rawItem.url)
                                    .withRelevancy(rawItem.relevancy)
                                    .build());
                }))
                .listener(new LogStepListener())
                .build();
    }

    private FieldSetMapper<RawNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawNamedPresetMapper(SourceColumnsFactory.createFor(GENE_PRODUCT_COLUMNS));
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> rawPresetValidator() {
        return new RawNamedPresetValidator();
    }
}
