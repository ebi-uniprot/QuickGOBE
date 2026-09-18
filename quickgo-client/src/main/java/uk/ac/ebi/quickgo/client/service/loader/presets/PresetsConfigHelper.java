package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.springframework.batch.item.file.BufferedReaderFactory;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Defines helper methods used when reading and populating preset information via Spring Batch.
 *
 * @see PresetsConfig
 *
 * Created 01/09/16
 * @author Edd
 */
public class PresetsConfigHelper {
    private static final Logger LOGGER = getLogger(PresetsConfigHelper.class);

    public static <S, T> ItemProcessor<S, T> compositeItemProcessor(ItemProcessor<?, ?>... delegates) {
        List<ItemProcessor<?, ?>> processors = Stream.of(delegates).collect(Collectors.toList());

        CompositeItemProcessor<S, T> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);

        return compositeProcessor;
    }

    public static <T> MultiResourceItemReader<T> rawPresetMultiFileReader(
            Resource[] resources, FlatFileItemReader<T> itemReader) {
        MultiResourceItemReader<T> reader = new MultiResourceItemReader<>();
        setResourceComparator(reader);
        reader.setResources(resources);
        reader.setDelegate(itemReader);
        return reader;
    }

    public static <T> FlatFileItemReader<T> fileReader(FieldSetMapper<T> fieldSetMapper) {
        FlatFileItemReader<T> reader = new FlatFileItemReader<>();

        DefaultLineMapper<T> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(PresetsConfig.TAB_DELIMITER));
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);
        reader.setBufferedReaderFactory(new ResourceBufferedReaderFactory());

        return reader;
    }

    static JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    /**
     * Since the resources are zipped files loaded from an input stream, we cannot
     * process files in an order based on their names; instead we process them in
     * the order they were specified in the properties file.
     *
     * @param reader the resource reader
     */
    private static <T> void setResourceComparator(MultiResourceItemReader<T> reader) {
        reader.setComparator((o1, o2) -> 0);
    }

    private static class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }

    public static ItemReader<RawNamedPreset> topItemsFromRESTReader(
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

    private static class ResourceBufferedReaderFactory implements BufferedReaderFactory {
        public BufferedReader create(Resource resource, String encoding) throws IOException {
            if(resource.getFilename().toLowerCase().endsWith(".gz")) {
                return new BufferedReader(new InputStreamReader(new GZIPInputStream(resource.getInputStream()), encoding));
            }
            return new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding));
        }
    }
}
