package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.SolrCrudRepoWriter;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.io.File;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created 02/12/15
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@Import({OntologyRepoConfig.class})
public class OntologyConfig {
    static final String ONTOLOGY_INDEXING_JOB_NAME = "ontologyIndexingJob";
    static final String ONTOLOGY_INDEXING_STEP_NAME = "ontologyIndexStep";

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private OntologyRepository ontologyRepository;

    @Value("${indexing.ontology.chunk.size:500}")
    private int chunkSize;

    @Value("${indexing.ontology.skip.limit:100}")
    private int skipLimit;

    @Value("${indexing.ontology.source}")
    private String sourceFile;

    @Bean
    public Job ontologyJob(Step ontologyStep) {
        return jobBuilders.get(ONTOLOGY_INDEXING_JOB_NAME)
                .start(ontologyStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step ontologyStep() {
        return stepBuilders
                .get(ONTOLOGY_INDEXING_STEP_NAME)
                // read and process items in chunks of the following size
                .<OntologyDocument, OntologyDocument>chunk(chunkSize)
                .reader(ontologyReader())
                .faultTolerant()
                .skip(DocumentReaderException.class)
                .skipLimit(skipLimit)
                .writer(ontologyWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    ItemWriter<OntologyDocument> ontologyWriter() {
        return new SolrCrudRepoWriter<>(ontologyRepository);
    }

    @Bean
    OntologyReader ontologyReader() {
        return new OntologyReader(new File(sourceFile));
    }

    private LogJobListener logJobListener() {
        return new LogJobListener();
    }

    private LogStepListener logStepListener() {
        return new LogStepListener();
    }
}