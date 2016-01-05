package uk.ac.ebi.quickgo.repo.write.job;

import uk.ac.ebi.quickgo.config.RepoConfig;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.repo.reader.ODocReader;
import uk.ac.ebi.quickgo.repo.write.IndexerProperties;
import uk.ac.ebi.quickgo.repo.write.listener.LogJobListener;
import uk.ac.ebi.quickgo.repo.write.listener.LogStepListener;
import uk.ac.ebi.quickgo.repo.write.writer.SolrCrudRepoWriter;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 *
 * Created 02/12/15
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@Import({IndexerProperties.class, RepoConfig.class})
public class IndexingJobConfig {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private OntologyRepository ontologyRepository;

    @Autowired
    private IndexerProperties indexerProperties;

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
                .<OntologyDocument, OntologyDocument>chunk(indexerProperties.getOntologyChunkSize())
                .reader(reader())
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
        return new ODocReader(new File(indexerProperties.getOntologySourceFile()));
    }

}
