package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
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
@Import({OntologyRepoConfig.class, OntologySiteMapConfig.class})
public class OntologyConfig {
    static final String ONTOLOGY_INDEXING_JOB_NAME = "ontologyIndexingJob";
    static final String ONTOLOGY_INDEXING_STEP_NAME = "ontologyIndexStep";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private OntologyRepository ontologyRepository;

    @Autowired
    private SiteMapItemWriter siteMapOntologyWriter;

    @Autowired
    private SiteMapStepListener siteMapStepListener;

    @Value("${indexing.ontology.chunk.size:500}")
    private int chunkSize;

    @Value("${indexing.ontology.skip.limit:100}")
    private int skipLimit;

    @Value("${indexing.ontology.source}")
    private String sourceFile;

    @Bean
    public Job ontologyJob(Step ontologyStep) {
        return new JobBuilder(ONTOLOGY_INDEXING_JOB_NAME, jobRepository)
                .start(ontologyStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step ontologyStep() {
        return new StepBuilder(ONTOLOGY_INDEXING_STEP_NAME, jobRepository)
                // read and process items in chunks of the following size
                .<OntologyDocument, OntologyDocument>chunk(chunkSize, transactionManager)
                .reader(ontologyReader())
                .faultTolerant()
                .skip(DocumentReaderException.class)
                .skipLimit(skipLimit)
                .writer(compositeOntologyWriter())
                .listener(logStepListener())
                .listener(siteMapStepListener)
                .build();
    }

    @Bean
    OntologyReader ontologyReader() {
        try {
            return OntologyReader.buildReader(new File(sourceFile));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load ontology files from " + sourceFile, e);
        }
    }

    private ItemWriter<OntologyDocument> compositeOntologyWriter() {
        List<ItemWriter<? super OntologyDocument>> writers = new ArrayList<>();
        writers.add(entity -> ontologyRepository.saveAll(entity));
        writers.add(siteMapOntologyWriter);

        CompositeItemWriter<OntologyDocument> compositeItemWriter = new CompositeItemWriter<>();
        compositeItemWriter.setDelegates(writers);
        return compositeItemWriter;
    }

    private LogJobListener logJobListener() {
        return new LogJobListener();
    }

    private LogStepListener logStepListener() {
        return new LogStepListener();
    }
}
