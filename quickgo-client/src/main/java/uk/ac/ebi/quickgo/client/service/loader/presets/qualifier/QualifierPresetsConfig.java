package uk.ac.ebi.quickgo.client.service.loader.presets.qualifier;

import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.model.presets.impl.PresetItemBuilder;
import uk.ac.ebi.quickgo.client.service.loader.presets.LogStepListener;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsCommonConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig.SKIP_LIMIT;

/**
 * Exposes the {@link Step} bean that is used to read and populate information relating to the qualifier preset data.
 * The required taxonomy information is only the top N qualifier IDs. No further information is required.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class QualifierPresetsConfig {
    private static final Logger LOGGER = getLogger(QualifierPresetsConfig.class);
    public static final String QUALIFIER_LOADING_STEP_NAME = "QualifierReadingStep";
    private static final String QUALIFIER = "qualifier";

    @Bean
    public Step taxonStep(
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
    private ItemWriter<RawNamedPreset> rawPresetWriter(CompositePresetImpl presets) {
        return rawItemList -> {
            rawItemList.forEach(rawItem -> {
                presets.taxonBuilder.addPreset(
                        PresetItemBuilder.createWithName(rawItem.name)
                                .withRelevancy(rawItem.relevancy)
                                .build());
            });
        };
    }

    private ItemReader<RawNamedPreset> topItemsFromRESTReader(
            RESTFilterConverterFactory converterFactory,
            String field) {
        FilterRequest request = FilterRequest.newBuilder().addProperty(field).build();

        try {
            List<String> relevantItems = converterFactory.<List<String>>convert(request).getConvertedValue();
            Iterator<String> relevantItemIterator = relevantItems.iterator();
            AtomicInteger position = new AtomicInteger(0);

            return () -> {
                if (relevantItemIterator.hasNext()) {
                    RawNamedPreset rawNamedPreset = new RawNamedPreset();
                    rawNamedPreset.name = relevantItemIterator.next();
                    rawNamedPreset.relevancy = position.getAndIncrement();
                    return rawNamedPreset;
                } else {
                    return null;
                }
            };
        } catch (RetrievalException | IllegalStateException e) {
            LOGGER.error("Failed to retrieve via REST call the relevant '" + field + "' values: ", e);
        }

        return () -> null;
    }
}
