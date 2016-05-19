package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.common.batch.listener.LogJobListener;
import uk.ac.ebi.quickgo.common.batch.listener.LogStepListener;
import uk.ac.ebi.quickgo.common.batch.listener.SkipLoggerListener;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
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

/**
 * Created 18/05/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
public class OntologyTraversalConfig {
    private static final String ONTOLOGY_TRAVERSAL_LOADING_JOB_NAME = "OntologyTraversalReadingJob";
    private static final String ONTOLOGY_TRAVERSAL_LOADING_STEP_NAME = "OntologyTraversalReadingStep";
    private static final String TAB = "\t";

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Value("${ontology.traversal.source}")
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
    public Job ontologyGraphBuildJob(OntologyGraph ontologyGraph) throws IOException {
        return jobBuilders.get(ONTOLOGY_TRAVERSAL_LOADING_JOB_NAME)
                .start(ontologyGraphBuildStep(ontologyGraph))
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step ontologyGraphBuildStep(OntologyGraph ontologyGraph) throws IOException {
        return stepBuilders.get(ONTOLOGY_TRAVERSAL_LOADING_STEP_NAME)
                .<OntologyRelationshipTuple, OntologyRelationshipTuple>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .<OntologyRelationshipTuple>reader(ontologyTraversalMultiFileReader())
                .processor(ontologyRelationshipCompositeProcessor())
                .writer(ontologyGraphPopulator(ontologyGraph))
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    private SkipLoggerListener<OntologyRelationshipTuple, OntologyRelationshipTuple> skipLogListener() {
        return new SkipLoggerListener<>();
    }

    @Bean
    ItemWriter<OntologyRelationshipTuple> ontologyGraphPopulator(OntologyGraph ontologyGraph) {
        return new OntologyGraphPopulator(ontologyGraph);
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private StepExecutionListener logStepListener() {
        return new LogStepListener();
    }

    @Bean
    ItemProcessor<OntologyRelationshipTuple, OntologyRelationshipTuple> ontologyRelationshipCompositeProcessor() {
        List<ItemProcessor<OntologyRelationshipTuple, OntologyRelationshipTuple>> processors = new ArrayList<>();

        processors.add(ontologyRelationshipValidator());

        CompositeItemProcessor<OntologyRelationshipTuple, OntologyRelationshipTuple> compositeProcessor =
                new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);

        return compositeProcessor;
    }

    private ItemProcessor<OntologyRelationshipTuple, OntologyRelationshipTuple> ontologyRelationshipValidator() {
        return new OntologyRelationshipValidator();
    }

    @Bean
    MultiResourceItemReader<OntologyRelationshipTuple> ontologyTraversalMultiFileReader() throws IOException {
        MultiResourceItemReader<OntologyRelationshipTuple> reader = new MultiResourceItemReader<>();

        GZIPResource[] zippedResources = new GZIPResource[resources.length];
        for (int i = 0; i < resources.length; i++) {
            zippedResources[i] = new GZIPResource(resources[i]);
        }

        reader.setResources(zippedResources);
        reader.setDelegate(ontologyTraversalSingleFileReader());
        return reader;
    }

    @Bean
    FlatFileItemReader<OntologyRelationshipTuple> ontologyTraversalSingleFileReader() {
        FlatFileItemReader<OntologyRelationshipTuple> reader = new FlatFileItemReader<>();
        reader.setLineMapper(ontologyRelationshipLineMapper());
        reader.setLinesToSkip(headerLines);
        return reader;
    }

    @Bean
    LineMapper<OntologyRelationshipTuple> ontologyRelationshipLineMapper() {
        DefaultLineMapper<OntologyRelationshipTuple> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(TAB));
        lineMapper.setFieldSetMapper(geneProductFieldSetMapper());

        return lineMapper;
    }

    @Bean
    FieldSetMapper<OntologyRelationshipTuple> geneProductFieldSetMapper() {
        return new StringToOntologyTupleMapper();
    }

    private static class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }
}
