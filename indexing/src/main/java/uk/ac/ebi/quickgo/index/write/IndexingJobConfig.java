package uk.ac.ebi.quickgo.index.write;

import uk.ac.ebi.quickgo.index.reader.DocumentReaderException;
import uk.ac.ebi.quickgo.index.reader.ODocReader;
import uk.ac.ebi.quickgo.index.write.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.write.listener.LogStepListener;
import uk.ac.ebi.quickgo.index.write.writer.SolrCrudRepoWriter;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class IndexingJobConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingJobConfig.class);

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
    public Job job() {
        return jobBuilders.get("indexingJob")
                .start(step())
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilders
                .get("readThenWriteToRepoStep")
                // read and process items in chunks of the following size
                .<OntologyDocument, OntologyDocument>chunk(chunkSize)
                .reader(reader())
                .faultTolerant()
                .skip(DocumentReaderException.class)
                .skipLimit(skipLimit)
                .writer(writer())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public ItemWriter<OntologyDocument> writer() {
        return new SolrCrudRepoWriter<>(ontologyRepository);
    }

    @Bean
    public LogJobListener logJobListener() {
        return new LogJobListener();
    }

    @Bean
    public LogStepListener logStepListener() {
        return new LogStepListener();
    }

    @Bean
    public ODocReader reader() {
        return new ODocReader(new File(sourceFile));
    }
}
