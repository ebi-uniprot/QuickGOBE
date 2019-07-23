package uk.ac.ebi.quickgo.index.annotation;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

/**
 * Sets up batch jobs for annotation indexing.
 *
 * Created 20/04/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@Import({AnnotationRepoConfig.class, CoTermsConfig.class, AnnotationConfig.class})
public class AnnotationIndexingConfig {
    private static final String ANNOTATION_INDEXING_JOB_NAME = "annotationIndexingJob";
    private static final String ANNOTATION_INDEXING_STEP_NAME = "annotationIndexStep";

    @Value("${indexing.annotation.source}")
    private Resource[] resources;
    @Value("${indexing.annotation.chunk.size:500}")
    private int chunkSize;
    @Value("${indexing.coterms.chunk.size:1}")
    private int cotermsChunk;
    @Value("${indexing.annotation.header.lines:21}")
    private int headerLines;
    @Value("${indexing.annotation.skip.limit:100}")
    private int skipLimit;
    @Value("${indexing.coterm.loginterval:1000}")
    private int coTermLogInterval;
    @Value("${indexing.annotation.retries.initialInterval:5000}")
    private int initialBackOffInterval;
    @Value("${indexing.annotation.retries.maxInterval:20000}")
    private int maxBackOffInterval;
    @Value("${indexing.annotation.retries.retryLimit:20}")
    private int retryLimit;

    @Autowired
    private SolrTemplate annotationTemplate;
    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private Step coTermManualSummarizationStep;
    @Autowired
    private Step coTermAllSummarizationStep;
    @Autowired
    private CoTermsAggregationWriter coTermsManualAggregationWriter;
    @Autowired
    private CoTermsAggregationWriter coTermsAllAggregationWriter;
    @Autowired
    private MultiResourceItemReader<Annotation> annotationMultiFileReader;
    @Autowired
    private ItemWriter<AnnotationDocument> annotationSolrServerWriter;
    @Autowired
    private ItemProcessor<Annotation, AnnotationDocument> annotationDocConverter;
    @Autowired
    private ItemProcessor<Annotation, Annotation> annotationValidator;
    @Autowired
    private ItemProcessor<AnnotationDocument, AnnotationDocument> annotationShardGenerator;

    @Bean
    public Job annotationJob() {
        return jobBuilders.get(ANNOTATION_INDEXING_JOB_NAME)
                          .start(annotationIndexingStep())
                          .next(coTermManualSummarizationStep)
                          .next(coTermAllSummarizationStep)
                          .listener(logJobListener())
                          // commit the documents to the solr server
                          .listener(new JobExecutionListener() {
                              @Override public void beforeJob(JobExecution jobExecution) {}

                              @Override public void afterJob(JobExecution jobExecution) {
                                  annotationTemplate.commit(SolrCollectionName.ANNOTATION);
                              }
                          })
                          .build();
    }

    private Step annotationIndexingStep() {
        return stepBuilders.get(ANNOTATION_INDEXING_STEP_NAME)
                .<Annotation, AnnotationDocument>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .retry(HttpSolrClient.RemoteSolrException.class)
                .retryLimit(retryLimit)
                .backOffPolicy(backOffPolicy())
                .<Annotation>reader(annotationMultiFileReader)
                .processor(annotationCompositeProcessor())
                .<AnnotationDocument>writer(compositeAnnotationWriter())
                .listener(logWriteRateListener())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    private BackOffPolicy backOffPolicy() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialBackOffInterval);
        backOffPolicy.setMaxInterval(maxBackOffInterval);
        return backOffPolicy;
    }

    private ItemWriter<AnnotationDocument> compositeAnnotationWriter() {
        CompositeItemWriter<AnnotationDocument> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<? super AnnotationDocument>> writerList = new ArrayList<>();
        writerList.add(annotationSolrServerWriter);
        writerList.add(coTermsManualAggregationWriter);
        writerList.add(coTermsAllAggregationWriter);
        compositeItemWriter.setDelegates(writerList);
        return compositeItemWriter;
    }

    private ItemProcessor<Annotation, AnnotationDocument> annotationCompositeProcessor() {
        List<ItemProcessor<?, ?>> processors = new ArrayList<>();
        processors.add(annotationValidator);
        processors.add(annotationDocConverter);
        processors.add(annotationShardGenerator);

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
