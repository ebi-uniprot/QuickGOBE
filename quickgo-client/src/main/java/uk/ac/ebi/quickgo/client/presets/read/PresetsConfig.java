package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.RawAssignedByPreset;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.RawAssignedByPresetValidator;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.StringToAssignedByMapper;
import uk.ac.ebi.quickgo.common.SearchableDocumentFields;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configuration for reading ontology relationship source files and populating a corresponding
 * graph.
 *
 * Created 18/05/16
 * @author Edd
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.rest"})
@EnableBatchProcessing
public class PresetsConfig {

    private static final Logger LOGGER = getLogger(PresetsConfig.class);
    private static final String PRESET_LOADING_JOB_NAME = "PresetReadingJob";
    private static final String ASSIGNED_BY_LOADING_STEP_NAME = "AssignedByReadingStep";
    private static final String TAB = "\t";
    private static final int SKIP_LIMIT = 0;
    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Value("#{'${assignedBy.preset.source:}'.split(',')}")
    private Resource[] assignedByResources;
    @Value("${preset.chunk.size:500}")
    private int chunkSize;
    @Value("${assignedBy.preset.header.lines:1}")
    private int assignedByHeaderLines;

    @Bean
    public CompositePreset presets() {
        return new CompositePreset();
    }

    @Bean
    public Job presetsBuildJob(CompositePreset preset, RESTFilterConverterFactory converterFactory) {
        return jobBuilders.get(PRESET_LOADING_JOB_NAME)
                .start(assignedByPresetBuildStep(preset, converterFactory))
                .listener(logJobListener())
                .build();
    }

    @Bean
    public SearchableDocumentFields searchableDocumentFields() {
        return new NoSearchablePresetDocumentFields();
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private Step assignedByPresetBuildStep(CompositePreset preset, RESTFilterConverterFactory converterFactory) {
        FlatFileItemReader<RawAssignedByPreset> itemReader = fileReader(rawAssignedByPresetFieldSetMapper());
        itemReader.setLinesToSkip(assignedByHeaderLines);

        return stepBuilders.get(ASSIGNED_BY_LOADING_STEP_NAME)
                .<RawAssignedByPreset, RawAssignedByPreset>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(SKIP_LIMIT)
                .<RawAssignedByPreset>reader(rawPresetMultiFileReader(assignedByResources, itemReader))
                .processor(compositeItemProcessor(assignedByValidator(), assignedByRelevancyFetcher(converterFactory)))
                .writer(list -> list.forEach(rawPreset ->
                        preset.assignedBy
                                .presets
                                .add(new PresetItem(rawPreset.name, rawPreset.description))
                ))
                .listener(new LogStepListener())
                .build();
    }

    private <S, T> ItemProcessor<S, T> compositeItemProcessor(ItemProcessor<?, ?>... delegates) {
        List<ItemProcessor<?, ?>> processors = Stream.of(delegates).collect(Collectors.toList());

        CompositeItemProcessor<S, T> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);

        return compositeProcessor;
    }

    private <T> MultiResourceItemReader<T> rawPresetMultiFileReader(
            Resource[] resources,
            FlatFileItemReader<T> itemReader) {
        MultiResourceItemReader<T> reader = new MultiResourceItemReader<>();

        setResourceComparator(reader);

        try {
            GZIPResource[] zippedResources = new GZIPResource[resources.length];
            for (int i = 0; i < resources.length; i++) {
                zippedResources[i] = new GZIPResource(resources[i]);
            }

            reader.setResources(zippedResources);
            reader.setDelegate(itemReader);
        } catch (IOException e) {
            LOGGER.error(
                    "Failed to load preset information for " + Stream.of(resources) + ". " +
                            "No corresponding information for this preset will be available.", e);
        }

        return reader;
    }

    private <T> FlatFileItemReader<T> fileReader(FieldSetMapper<T> fieldSetMapper) {
        FlatFileItemReader<T> reader = new FlatFileItemReader<>();

        DefaultLineMapper<T> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(TAB));
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(assignedByHeaderLines);

        return reader;
    }

    private FieldSetMapper<RawAssignedByPreset> rawAssignedByPresetFieldSetMapper() {
        return new StringToAssignedByMapper();
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> assignedByValidator() {
        return new RawAssignedByPresetValidator();
    }

    private ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> assignedByRelevancyFetcher
            (RESTFilterConverterFactory converterFactory) {
        FilterRequest assignedBy = FilterRequest.newBuilder().addProperty("assignedBy").build();
        ConvertedFilter<List<String>> convert = converterFactory.convert(assignedBy);

        return new RawAssignedByPresetTopN(assignedByName -> convert.getConvertedValue().contains(assignedByName));
    }

    /**
     * Since the resources are zipped files loaded from an input stream, we cannot
     * process files in an order based on their names; instead we process them in
     * the order they were specified in the properties file.
     *
     * @param reader the resource reader
     */
    private <T> void setResourceComparator(MultiResourceItemReader<T> reader) {
        reader.setComparator((o1, o2) -> 0);
    }

    private static class NoSearchablePresetDocumentFields implements SearchableDocumentFields {
        @Override public boolean isDocumentSearchable(String field) {
            return false;
        }

        @Override public Stream<String> searchableDocumentFields() {
            return Stream.empty();
        }
    }

    private static class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }
}
