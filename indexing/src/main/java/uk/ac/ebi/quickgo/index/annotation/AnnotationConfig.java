package uk.ac.ebi.quickgo.index.annotation;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.CompositeStepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTerm;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsAggregationWriter;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig;
import uk.ac.ebi.quickgo.index.common.SolrServerWriter;
import uk.ac.ebi.quickgo.index.common.listener.ItemRateWriterListener;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.index.common.listener.SkipLoggerListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_ALL_SUMMARIZATION_STEP;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_MANUAL_SUMMARIZATION_STEP;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.TAB;

/**
 * Sets up batch jobs for annotation indexing.
 *
 * Created 20/04/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@Import({AnnotationRepoConfig.class, CoTermsConfig.class})
public class AnnotationConfig {
    static final String ANNOTATION_INDEXING_JOB_NAME = "annotationIndexingJob";
    static final String ANNOTATION_INDEXING_STEP_NAME = "annotationIndexStep";

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

    @Autowired
    private SolrTemplate annotationTemplate;
    @Autowired
    private JobBuilderFactory jobBuilders;
    @Autowired
    private StepBuilderFactory stepBuilders;
    @Autowired
    private CoTermsAggregationWriter coTermsManualAggregationWriter;
    @Autowired
    private CoTermsAggregationWriter coTermsAllAggregationWriter;
    @Autowired
    private ItemProcessor<String, List<CoTerm>> coTermsManualCalculator;
    @Autowired
    private ItemProcessor<String, List<CoTerm>> coTermsAllCalculator;
    @Autowired
    private ItemReader<String> coTermsManualReader;
    @Autowired
    private ItemReader<String> coTermsAllReader;
    @Autowired
    private ItemWriter<List<CoTerm>> coTermsManualStatsWriter;
    @Autowired
    private ItemWriter<List<CoTerm>> coTermsAllStatsWriter;

    @Bean
    public Job annotationJob() {
        return jobBuilders.get(ANNOTATION_INDEXING_JOB_NAME)
                .start(annotationIndexingStep())
                .next(coTermManualSummarizationStep())
                .next(coTermAllSummarizationStep())
                .listener(logJobListener())
                // commit the documents to the solr server
                .listener(new JobExecutionListener() {
                    @Override public void beforeJob(JobExecution jobExecution) {}
                    @Override public void afterJob(JobExecution jobExecution) {
                        annotationTemplate.commit();
                    }
                })
                .build();
    }

    @Bean
    public Step annotationIndexingStep() {
        return stepBuilders.get(ANNOTATION_INDEXING_STEP_NAME)
                .<Annotation, AnnotationDocument>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .<Annotation>reader(annotationMultiFileReader())
                .processor(annotationCompositeProcessor())
                .<AnnotationDocument>writer(compositeAnnotationWriter())
                .listener(logWriteRateListener())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    @Bean
    public Step coTermManualSummarizationStep() {
        return stepBuilders.get(CO_TERM_MANUAL_SUMMARIZATION_STEP)
                .<String, List<CoTerm>>chunk(cotermsChunk)
                .reader(coTermsManualReader)
                .processor(coTermsManualCalculator)
                .writer(coTermsManualStatsWriter)
                .listener(logStepListener())
                .listener(logWriteRateListener(coTermLogInterval))
                .listener(skipLogListener())
                .build();
    }

    @Bean
    public Step coTermAllSummarizationStep() {
        return stepBuilders.get(CO_TERM_ALL_SUMMARIZATION_STEP)
                .<String, List<CoTerm>>chunk(cotermsChunk)
                .reader(coTermsAllReader)
                .processor(coTermsAllCalculator)
                .writer(coTermsAllStatsWriter)
                .listener(logStepListener())
                .listener(logWriteRateListener(coTermLogInterval))
                .listener(skipLogListener())
                .build();
    }

    private ItemWriteListener<QuickGODocument> logWriteRateListener() {
        return new ItemRateWriterListener<>(Instant.now());
    }

    private ItemWriteListener<QuickGODocument> logWriteRateListener(final int writeInterval) {
        return new ItemRateWriterListener<>(Instant.now(), writeInterval);
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

    @Bean
    MultiResourceItemReader<Annotation> annotationMultiFileReader() {
        MultiResourceItemReader<Annotation> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(annotationSingleFileReader());
        return reader;
    }

    @Bean
    FlatFileItemReader<Annotation> annotationSingleFileReader() {
        FlatFileItemReader<Annotation> reader = new FlatFileItemReader<>();
        reader.setLineMapper(annotationLineMapper());
        reader.setLinesToSkip(headerLines);
        return reader;
    }

    @Bean
    LineMapper<Annotation> annotationLineMapper() {
        DefaultLineMapper<Annotation> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(annotationLineTokenizer());
        lineMapper.setFieldSetMapper(annotationFieldSetMapper());
        return lineMapper;
    }

    @Bean
    LineTokenizer annotationLineTokenizer() {
        return new DelimitedLineTokenizer(TAB);
    }

    @Bean
    FieldSetMapper<Annotation> annotationFieldSetMapper() {
        return new StringToAnnotationMapper();
    }

    @Bean
    ItemProcessor<Annotation, AnnotationDocument> annotationDocConverter() {
        return new AnnotationDocumentConverter();
    }

    @Bean
    ItemProcessor<Annotation, Annotation> annotationValidator() {
        Validator<Annotation> annotationValidator =
                new AnnotationValidator();
        return new ValidatingItemProcessor<>(annotationValidator);
    }

    @Bean
    ItemProcessor<Annotation, AnnotationDocument> annotationCompositeProcessor() {
        List<ItemProcessor<Annotation, ?>> processors = new ArrayList<>();
        processors.add(annotationValidator());
        processors.add(annotationDocConverter());

        CompositeItemProcessor<Annotation, AnnotationDocument> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);
        return compositeProcessor;
    }

    @Bean
    ItemWriter<AnnotationDocument> annotationSolrServerWriter() {
        return new SolrServerWriter<>(annotationTemplate.getSolrClient());
    }

    @Bean
    ItemWriter<AnnotationDocument> compositeAnnotationWriter() {
        CompositeItemWriter<AnnotationDocument> compositeItemWriter = new CompositeItemWriter<>();
        List<ItemWriter<? super AnnotationDocument>> writerList = new ArrayList<>();
        writerList.add(annotationSolrServerWriter());
        writerList.add(coTermsManualAggregationWriter);
        writerList.add(coTermsAllAggregationWriter);
        compositeItemWriter.setDelegates(writerList);
        return compositeItemWriter;
    }
}
