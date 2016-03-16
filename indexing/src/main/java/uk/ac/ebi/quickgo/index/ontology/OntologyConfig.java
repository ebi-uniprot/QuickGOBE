package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.SolrCrudRepoWriter;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

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

import java.io.File;

/**
 * Created 02/12/15
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@Import({RepoConfig.class})
public class OntologyConfig {
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
        return jobBuilders.get("indexingJob")
                .start(ontologyStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step ontologyStep(OntologyReader reader) {
        return stepBuilders
                .get("readThenWriteToRepoStep")
                // read and process items in chunks of the following size
                .<OntologyDocument, OntologyDocument>chunk(chunkSize)
                .reader(reader)
                .faultTolerant()
                .skip(DocumentReaderException.class)
                .skipLimit(skipLimit)
                .writer(ontologyWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public ItemWriter<OntologyDocument> ontologyWriter() {
        return new SolrCrudRepoWriter<>(ontologyRepository);
    }

    @Bean
    public OntologyReader ontologyReader() {
        return new OntologyReader(new File(sourceFile));
    }

    @Bean
    public LogJobListener logJobListener() {
        return new LogJobListener();
    }

    @Bean
    public LogStepListener logStepListener() {
        return new LogStepListener();
    }
}