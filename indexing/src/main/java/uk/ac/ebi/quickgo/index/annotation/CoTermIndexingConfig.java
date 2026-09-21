package uk.ac.ebi.quickgo.index.annotation;

import org.apache.solr.client.solrj.SolrServerException;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsAggregationWriter;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig;
import uk.ac.ebi.quickgo.index.common.listener.ItemRateWriterListener;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.index.common.listener.SkipLoggerListener;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.support.CompositeItemWriter;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.apache.solr.client.solrj.SolrClient;

/**
 * Sets up batch jobs for annotation indexing.
 *
 * Created 20/04/16
 * @author Tony Wardell
 */
@Configuration
@Import({AnnotationRepoConfig.class, CoTermsConfig.class, AnnotationConfig.class})
public class CoTermIndexingConfig {
    static final String COTERM_INDEXING_JOB_NAME = "coTermIndexingJob";
    static final String ANNOTATION_READING_STEP_NAME = "annotationReadingStep";

    @Value("${indexing.annotation.source}")
    private Resource[] resources;
    @Value("${indexing.annotation.chunk.size:500}")
    private int chunkSize;
    @Value("${indexing.annotation.skip.limit:100}")
    private int skipLimit;
    @Value("${indexing.annotation.header.lines:21}")
    private int headerLines;

    @Autowired
    private SolrClient annotationSolrClient;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private Step coTermManualSummarizationStep;
    @Autowired
    private Step coTermAllSummarizationStep;
    @Autowired
    private MultiResourceItemReader<Annotation> annotationMultiFileReader;
    @Autowired
    private CoTermsAggregationWriter coTermsManualAggregationWriter;
    @Autowired
    private CoTermsAggregationWriter coTermsAllAggregationWriter;
    @Autowired
    private ItemProcessor<Annotation, AnnotationDocument> annotationDocConverter;
    @Autowired
    private ItemProcessor<Annotation, Annotation> annotationValidator;

    @Bean
    public Job coTermsOnlyJob() {
        return new JobBuilder(COTERM_INDEXING_JOB_NAME, jobRepository)
                          .start(annotationReadingStep())
                          .next(coTermManualSummarizationStep)
                          .next(coTermAllSummarizationStep)
                          .listener(logJobListener())
                          // commit the documents to the solr server
                          .listener(new JobExecutionListener() {
                              @Override public void beforeJob(JobExecution jobExecution) {}

                              @Override public void afterJob(JobExecution jobExecution) {
                                  try {
                                      annotationSolrClient.commit(SolrCollectionName.ANNOTATION);
                                  } catch (SolrServerException | IOException e) {
                                      throw new RuntimeException("Failed to commit annotation Solr index", e);
                                  }
                              }
                          })
                          .build();
    }

    private Step annotationReadingStep() {
        return new StepBuilder(ANNOTATION_READING_STEP_NAME, jobRepository)
                .<Annotation, AnnotationDocument>chunk(chunkSize, transactionManager)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .<Annotation>reader(annotationMultiFileReader)
                .processor(annotationCompositeProcessor())
                .<AnnotationDocument>writer(compositeAnnotationWriter())
                .listener(logWriteRateListener())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    /**
     * Compared to the AnnotationConfig, there is no solr writer included.
     * @return writer to the co terms aggregation instances.
     */
    private ItemWriter<AnnotationDocument> compositeAnnotationWriter() {
        CompositeItemWriter<AnnotationDocument> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<? super AnnotationDocument>> writerList = new ArrayList<>();
        writerList.add(coTermsManualAggregationWriter);
        writerList.add(coTermsAllAggregationWriter);
        compositeItemWriter.setDelegates(writerList);
        return compositeItemWriter;
    }

    /**
     * Turn Annotation into AnnotationDocument instance.
     * @return validator and converter composite.
     */
    private ItemProcessor<Annotation, AnnotationDocument> annotationCompositeProcessor() {
        List<ItemProcessor<?, ?>> processors = new ArrayList<>();
        processors.add(annotationValidator);
        processors.add(annotationDocConverter);
        CompositeItemProcessor<Annotation, AnnotationDocument> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);
        return compositeProcessor;
    }

    private ItemWriteListener<QuickGODocument> logWriteRateListener() {
        return new ItemRateWriterListener<>(Instant.now());
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private StepExecutionListener logStepListener() {
        return new LogStepListener();
    }

    private SkipLoggerListener<Annotation, AnnotationDocument> skipLogListener() {
        return new SkipLoggerListener<>();
    }
}
