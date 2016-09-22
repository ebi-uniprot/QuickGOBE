package uk.ac.ebi.quickgo.client.service.loader.presets.taxon;

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
 * Exposes the {@link Step} bean that is used to read and populate information relating to the taxonomy preset data.
 * The required taxonomy information is only the top N taxon IDs. No further information is required.
 *
 * Created 01/09/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class})
public class TaxonPresetsConfig {
    private static final Logger LOGGER = getLogger(TaxonPresetsConfig.class);
    public static final String TAXON_LOADING_STEP_NAME = "TaxonReadingStep";
    private static final String TAXON_ID = "taxonId";

    @Bean
    public Step taxonStep(
            StepBuilderFactory stepBuilderFactory,
            Integer chunkSize,
            CompositePresetImpl presets,
            RESTFilterConverterFactory converterFactory) {

        return stepBuilderFactory.get(TAXON_LOADING_STEP_NAME)
                .<RawNamedPreset, RawNamedPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .reader(topTaxonsFromRESTReader(converterFactory))
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

    private ItemReader<RawNamedPreset> topTaxonsFromRESTReader(RESTFilterConverterFactory converterFactory) {
        FilterRequest taxonRequest = FilterRequest.newBuilder().addProperty(TAXON_ID).build();

        try {
            List<String> relevantTaxons = converterFactory.<List<String>>convert(taxonRequest).getConvertedValue();
            Iterator<String> iterator = relevantTaxons.iterator();
            AtomicInteger position = new AtomicInteger(0);

            return () -> {
                if (iterator.hasNext()) {
                    RawNamedPreset rawNamedPreset = new RawNamedPreset();
                    rawNamedPreset.name = iterator.next();
                    rawNamedPreset.relevancy = position.getAndIncrement();
                    return rawNamedPreset;
                } else {
                    return null;
                }
            };
        } catch (RetrievalException | IllegalStateException e) {
            LOGGER.error("Failed to retrieve via REST call the relevant 'assignedBy' values: ", e);
        }

        return () -> null;
    }
}
