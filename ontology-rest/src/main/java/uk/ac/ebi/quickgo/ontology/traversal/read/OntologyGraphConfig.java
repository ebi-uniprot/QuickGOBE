package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.common.batch.listener.LogJobListener;
import uk.ac.ebi.quickgo.common.batch.listener.LogStepListener;
import uk.ac.ebi.quickgo.common.batch.listener.SkipLoggerListener;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
@EnableBatchProcessing
public class OntologyGraphConfig {

    private static final Logger LOGGER = getLogger(OntologyGraphConfig.class);
    private static final String ONTOLOGY_TRAVERSAL_LOADING_JOB_NAME = "OntologyTraversalReadingJob";
    private static final String ONTOLOGY_TRAVERSAL_LOADING_STEP_NAME = "OntologyTraversalReadingStep";
    private static final String TAB = "\t";

    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Value("#{'${ontology.traversal.source:}'.split(',')}")
    private Resource[] resources;
    @Value("${ontology.traversal.chunk.size:500}")
    private int chunkSize;
    @Value("${ontology.traversal.header.lines:1}")
    private int headerLines;
    @Value("${ontology.traversal.skip.limit:100}")
    private int skipLimit;

    @Bean
    public OntologyGraph ontologyGraph() {
        return new OntologyGraph();
    }

    @Bean
    public Job ontologyGraphBuildJob(OntologyGraph ontologyGraph) {
        return jobBuilders.get(ONTOLOGY_TRAVERSAL_LOADING_JOB_NAME)
                    .start(ontologyGraphBuildStep(ontologyGraph))
                    .listener(logJobListener())
                    .build();
    }

    @Bean
    public Step ontologyGraphBuildStep(OntologyGraph ontologyGraph) {
        return stepBuilders.get(ONTOLOGY_TRAVERSAL_LOADING_STEP_NAME)
                .<OntologyRelationship, OntologyRelationship>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .<OntologyRelationship>reader(ontologyTraversalMultiFileReader())
                .processor(ontologyRelationshipCompositeProcessor())
                .writer(ontologyGraphPopulator(ontologyGraph))
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private SkipLoggerListener<OntologyRelationship, OntologyRelationship> skipLogListener() {
        return new SkipLoggerListener<>();
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private StepExecutionListener logStepListener() {
        return new LogStepListener();
    }

    private ItemProcessor<OntologyRelationship, OntologyRelationship> ontologyRelationshipValidator() {
        return new OntologyRelationshipValidator();
    }

    /**
     * Since the resources are zipped files loaded from an input stream, we cannot
     * process files in an order based on their names; instead we process them in
     * the order they were specified in the properties file.
     *
     * @param reader the resource reader
     */
    private void setResourceComparator(MultiResourceItemReader<OntologyRelationship> reader) {
        reader.setComparator((o1, o2) -> 0);
    }

    private static class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }

    @Bean
    ItemWriter<OntologyRelationship> ontologyGraphPopulator(OntologyGraph ontologyGraph) {
        return new OntologyGraphPopulator(ontologyGraph);
    }

    @Bean
    ItemProcessor<OntologyRelationship, OntologyRelationship> ontologyRelationshipCompositeProcessor() {
        List<ItemProcessor<OntologyRelationship, OntologyRelationship>> processors = new ArrayList<>();

        processors.add(ontologyRelationshipValidator());

        CompositeItemProcessor<OntologyRelationship, OntologyRelationship> compositeProcessor =
                new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);

        return compositeProcessor;
    }

    @Bean
    MultiResourceItemReader<OntologyRelationship> ontologyTraversalMultiFileReader() {
        MultiResourceItemReader<OntologyRelationship> reader = new MultiResourceItemReader<>();

        setResourceComparator(reader);

        try {
            GZIPResource[] zippedResources = new GZIPResource[resources.length];
            for (int i = 0; i < resources.length; i++) {
                zippedResources[i] = new GZIPResource(resources[i]);
            }

            reader.setResources(zippedResources);
            reader.setDelegate(ontologyTraversalSingleFileReader());
        } catch (IOException e) {
            LOGGER.error("Failed to populate ontology traversal graph, and therefore there will be " +
                    "no graph operations supported, e.g., closures and slimming: ", e);
        }

        return reader;
    }

    @Bean
    FlatFileItemReader<OntologyRelationship> ontologyTraversalSingleFileReader() {
        FlatFileItemReader<OntologyRelationship> reader = new FlatFileItemReader<>();
        reader.setLineMapper(ontologyRelationshipLineMapper());
        reader.setLinesToSkip(headerLines);
        return reader;
    }

    @Bean
    LineMapper<OntologyRelationship> ontologyRelationshipLineMapper() {
        DefaultLineMapper<OntologyRelationship> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(TAB));
        lineMapper.setFieldSetMapper(geneProductFieldSetMapper());

        return lineMapper;
    }

    @Bean
    FieldSetMapper<OntologyRelationship> geneProductFieldSetMapper() {
        return new StringToOntologyRelationshipMapper();
    }
}
