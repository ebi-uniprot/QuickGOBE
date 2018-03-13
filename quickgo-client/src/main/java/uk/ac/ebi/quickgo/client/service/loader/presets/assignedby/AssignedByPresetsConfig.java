package uk.ac.ebi.quickgo.client.service.loader.presets.assignedby;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.client.service.loader.support.DatabaseDescriptionConfig;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static uk.ac.ebi.quickgo.client.model.presets.PresetType.*;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfigHelper.topItemsFromRESTReader;
import static uk.ac.ebi.quickgo.client.service.loader.support.DatabaseDescriptionConfig.DB_DESCRIPTIONS_MAP;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the assignedBy preset data.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({DatabaseDescriptionConfig.class})
public class AssignedByPresetsConfig {
    public static final String ASSIGNED_BY_LOADING_STEP_NAME = "AssignedByReadingStep";
    public static final String ASSIGNED_BY_REST_KEY = "assignedBy";
    static Logger LOGGER = LoggerFactory.getLogger(AssignedByPresetsConfig.class);
    boolean logged = false;

    @Bean
    public Step assignedByStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets,
            PresetsCommonConfig.DbDescriptions dbDescriptions,
            RESTFilterConverterFactory converterFactory) {

        LOGGER.info("Logging db descriptions");
            DB_DESCRIPTIONS_MAP.forEach((k, e) -> LOGGER.info("Descriptions contains %s, %s", k, e));

        logged = true;

        return stepBuilderFactory.get(ASSIGNED_BY_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .reader(topItemsFromRESTReader(converterFactory, ASSIGNED_BY_REST_KEY))
                .processor(addDescription(dbDescriptions))
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
        return rawItemList -> rawItemList.forEach(rawItem -> {
            presets.addPreset(ASSIGNED_BY,
                    PresetItem.createWithName(rawItem.name)
                            .withRelevancy(rawItem.relevancy)
                            .build());
        });
    }

    private ItemProcessor<RawNamedPreset, RawNamedPreset> addDescription(
            PresetsCommonConfig.DbDescriptions dbDescriptions) {

        if (!logged) {
            dbDescriptions.dbDescriptions.forEach((k, e) -> LOGGER.info("Descriptions contains %s, %s", k, e));
        }

        return rawNamedPreset -> {
            if (DB_DESCRIPTIONS_MAP.containsKey(rawNamedPreset.id)) {
                LOGGER.info("Looking for %s", rawNamedPreset.id);
                rawNamedPreset.description = dbDescriptions.dbDescriptions.get(rawNamedPreset.id);
                return rawNamedPreset;
            } else {
                return rawNamedPreset;
            }
        };
    }
}
