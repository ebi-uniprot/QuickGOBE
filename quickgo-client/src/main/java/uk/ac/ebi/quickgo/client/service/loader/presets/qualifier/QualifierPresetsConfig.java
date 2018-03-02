package uk.ac.ebi.quickgo.client.service.loader.presets.qualifier;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetType;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.topItemsFromRESTReader;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the qualifier preset data.
 * The required qualifier information is only the top N qualifier IDs. No further information is required.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class QualifierPresetsConfig {
    public static final String QUALIFIER_LOADING_STEP_NAME = "QualifierReadingStep";
    public static final String QUALIFIER = "qualifier";

    @Bean
    public Step qualifierStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets,
            RESTFilterConverterFactory converterFactory) {

        return stepBuilderFactory.get(QUALIFIER_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .reader(topItemsFromRESTReader(converterFactory, QUALIFIER))
                .writer(rawPresetWriter(presets))
                .listener(new LogStepListener())
                .build();
    }

    /**
     * Write the list of {@link RawNamedPreset}s to the {@link CompositePresetImpl}
     * @param presets the presets to write to
     * @return the corresponding {@link ItemWriter}
     */
    ItemWriter<RawNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> {
            rawItemList.forEach(rawItem -> {
                presets.addPreset(PresetType.QUALIFIERS,
                        PresetItem.createWithName(uppercaseNots(rawItem.name))
                                .withRelevancy(rawItem.relevancy)
                                .build());
            });
        };
    }

    private String uppercaseNots(String name) {
        return name != null ? name.replace("not|", "NOT|") : null;
    }
}
