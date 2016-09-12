package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationRepoConfig;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoOccurringTerm;
import uk.ac.ebi.quickgo.index.annotation.coterms.*;
import uk.ac.ebi.quickgo.index.common.SolrServerWriter;
import uk.ac.ebi.quickgo.index.common.listener.ItemRateWriterListener;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.index.common.listener.SkipLoggerListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
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

import static uk.ac.ebi.quickgo.index.annotation.coterms.CoStatsConfiguration.COSTATS_ALL_COMPLETION_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoStatsConfiguration.COSTATS_MANUAL_COMPLETION_STEP_NAME;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.TAB;

/**
 * Sets up batch jobs for annotation indexing.
 *
 * Created 20/04/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
@Import({AnnotationRepoConfig.class, CoStatsConfiguration.class})
public class AnnotationConfig {
    static final String ANNOTATION_INDEXING_JOB_NAME = "annotationIndexingJob";
    static final String ANNOTATION_INDEXING_STEP_NAME = "annotationIndexStep";

    @Value("${indexing.annotation.source}")
    private Resource[] resources;

    @Value("${indexing.annotation.chunk.size:500}")
    private int chunkSize;

    @Value("${indexing.annotation.header.lines:21}")
    private int headerLines;

    @Value("${indexing.annotation.skip.limit:100}")
    private int skipLimit;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private SolrTemplate annotationTemplate;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    ItemProcessor<Annotation, Annotation> coStatsManual;

    @Autowired
    ItemProcessor<Annotation, Annotation> coStatsAll;

    @Autowired
    ItemProcessor<String, List<CoOccurringTerm>> coStatsManualItemProcessor;

    @Autowired
    ItemProcessor<String, List<CoOccurringTerm>> coStatsAllItemProcessor;

    @Autowired
    ItemReader<String> coStatsManualItemReader;

    @Autowired
    ItemWriter<List<CoOccurringTerm>> coStatManualFlatFileWriter;

    @Autowired
    ItemReader<String> coStatsAllItemReader;

    @Autowired
    ItemWriter<List<CoOccurringTerm>> coStatsAllFlatFileWriter;

    @Bean
    public Job annotationJob() {
        return jobBuilders.get(ANNOTATION_INDEXING_JOB_NAME)
                .start(annotationIndexingStep())
                .start(coStatsManualSummarizationStep())
                .start(coStatsAllSummarizationStep())
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
                .<AnnotationDocument>writer(annotationSolrServerWriter())
                .listener(logWriteRateListener())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }


    @Bean
    public Step coStatsManualSummarizationStep() {
        return stepBuilders.get(COSTATS_MANUAL_COMPLETION_STEP_NAME)
                .<String, List<CoOccurringTerm>>chunk(chunkSize)
                .<Annotation>reader(coStatsManualItemReader)
                .<CoOccurringTerm>processor(coStatsManualItemProcessor)
                .<List<CoOccurringTerm>>writer(coStatManualFlatFileWriter)
                .listener(logStepListener())
                .build();
    }

    @Bean
    public Step coStatsAllSummarizationStep() {
        return stepBuilders.get(COSTATS_ALL_COMPLETION_STEP_NAME)
                .<String, List<CoOccurringTerm>>chunk(chunkSize)
                .<Annotation>reader(coStatsAllItemReader)
                .<CoOccurringTerm>processor(coStatsAllItemProcessor)
                .<CoOccurringTerm>writer(coStatsAllFlatFileWriter)
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    private ItemWriteListener<QuickGODocument> logWriteRateListener() {
        return new ItemRateWriterListener<>(Instant.now());
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private StepListener logStepListener() {
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
        processors.add(coStatsManual);
        processors.add(coStatsAll);
        processors.add(annotationDocConverter());

        CompositeItemProcessor<Annotation, AnnotationDocument> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);

        return compositeProcessor;
    }

    @Bean
    ItemWriter<AnnotationDocument> annotationSolrServerWriter() {
        return new SolrServerWriter<>(annotationTemplate.getSolrClient());
    }
}
