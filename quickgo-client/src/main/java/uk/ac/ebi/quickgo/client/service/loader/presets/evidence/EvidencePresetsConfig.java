package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.evidence.PresetEvidenceItem;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.RawEvidenceNamedPresetColumnsBuilder
        .RawEvidenceNamedPresetColumnsImpl;

import java.util.Optional;
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
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.fileReader;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.rawPresetMultiFileReader;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the evidence preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class EvidencePresetsConfig {
    public static final String EVIDENCE_LOADING_STEP_NAME = "EvidenceReadingStep";

    @Value("#{'${evidence.preset.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${evidence.preset.header.lines:1}")
    private int headerLines;

    @Bean
    public Step evidenceStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets) {
        FlatFileItemReader<RawEvidenceNamedPreset> itemReader = fileReader(rawPresetFieldSetMapper());
        itemReader.setLinesToSkip(headerLines);

        return stepBuilderFactory.get(EVIDENCE_LOADING_STEP_NAME)
                .<RawEvidenceNamedPreset, RawEvidenceNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawEvidenceNamedPreset>reader(rawPresetMultiFileReader(resources, itemReader, Optional::of))
                .processor(rawPresetValidator())
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    /**
     * Write the list of {@link RawEvidenceNamedPreset}s to the {@link CompositePresetImpl}
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    private ItemWriter<RawEvidenceNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> rawItemList.forEach(rawItem ->
                presets.addPreset(PresetType.EVIDENCES,
                        PresetItem.createWithName(rawItem.name)
                                .withProperty(PresetItem.Property.ID.getKey(), rawItem.id)
                                .withRelevancy(rawItem.relevancy)
                                .withProperty(PresetEvidenceItem.Property.GO_EVIDENCE.getKey(), rawItem
                                        .goEvidence)
                                .build())
        );
    }

    private FieldSetMapper<RawEvidenceNamedPreset> rawPresetFieldSetMapper() {
        return new StringToRawEvidenceNamedPresetMapper(getColumns());
    }

    private ItemProcessor<RawEvidenceNamedPreset, RawEvidenceNamedPreset> rawPresetValidator() {
        return new RawEvidenceNamedPresetValidator();
    }

    private RawEvidenceNamedPresetColumnsImpl getColumns() {
        return RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(1)
                .withIdPosition(0)
                .withGoEvidence(2)
                .withRelevancyPosition(3)
                .build();
    }

}
