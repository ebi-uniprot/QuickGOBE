package uk.ac.ebi.quickgo.ontology.traversal;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Value("${ontology.traversal.source}")
    private Resource[] resources;

    @Value("${ontology.traversal.chunk.size:500}")
    private int chunkSize;

    @Value("${ontology.traversal.header.lines:17}")
    private int headerLines;

    @Value("${ontology.traversal.skip.limit:100}")
    private int skipLimit;

    @Bean
    public Job geneProductJob() {
        return jobBuilders.get(ONTOLOGY_TRAVERSAL_LOADING_JOB_NAME)
                .start(geneProductIndexingStep())
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step geneProductIndexingStep() {
        return stepBuilders.get(ONTOLOGY_TRAVERSAL_LOADING_STEP_NAME)
                .<GeneProduct, GeneProductDocument>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .listener(())
                .<GeneProduct>reader(ontologyTraversalMultiFileReader())
                .processor(geneProductCompositeProcessor())
                .writer(geneProductRepositoryWriter())
                .build();
    }

    @Bean
    MultiResourceItemReader<OntologyRelationshipTuple> ontologyTraversalMultiFileReader() {
        MultiResourceItemReader<OntologyRelationshipTuple> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(ontologyTraversalSingleFileReader());
        return reader;
    }

    @Bean
    FlatFileItemReader<OntologyRelationshipTuple> ontologyTraversalSingleFileReader() {
        FlatFileItemReader<OntologyRelationshipTuple> reader = new FlatFileItemReader<>();
        reader.setLineMapper(geneProductLineMapper());
        reader.setLinesToSkip(headerLines);
        return reader;
    }
}
