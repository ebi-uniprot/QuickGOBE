package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepoConfig;
import uk.ac.ebi.quickgo.index.common.SolrServerWriter;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
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
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

/**
 * Sets up batch jobs for gene product indexing.
 */
@Configuration
@EnableBatchProcessing
@Import({GeneProductRepoConfig.class})
public class GeneProductConfig {
    static final String GENE_PRODUCT_INDEXING_JOB_NAME = "geneProductIndexingJob";
    static final String GENE_PRODUCT_INDEXING_STEP_NAME = "geneProductIndexStep";

    private static final String COLUMN_DELIMITER = "\t";
    private static final String INTER_VALUE_DELIMITER = "\\|";
    private static final String INTRA_VALUE_DELIMITER = "=";
    private static final String SPECIFIC_VALUE_DELIMITER = ",";

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Value("${indexing.geneproduct.source}")
    private Resource[] resources;

    @Value("${indexing.geneproduct.chunk.size:500}")
    private int chunkSize;

    @Value("${indexing.geneproduct.header.lines:17}")
    private int headerLines;

    @Value("${indexing.geneproduct.skip.limit:100}")
    private int skipLimit;

    @Value("${indexing.geneproduct.retries.initialInterval:5000}")
    private int initialBackOffInterval;

    @Value("${indexing.geneproduct.retries.maxInterval:20000}")
    private int maxBackOffInterval;

    @Value("${indexing.geneproduct.retries.retryLimit:20}")
    private int retryLimit;

    @Autowired
    private SolrTemplate geneProductTemplate;

    @Bean
    public Job geneProductJob() {
        return jobBuilders.get(GENE_PRODUCT_INDEXING_JOB_NAME)
                .start(geneProductIndexingStep())
                .listener(logJobListener())
                // commit the documents to the solr server
                .listener(new JobExecutionListener() {
                    @Override public void beforeJob(JobExecution jobExecution) {}

                    @Override public void afterJob(JobExecution jobExecution) {
                        geneProductTemplate.commit();
                    }
                })
                .build();
    }

    @Bean
    public Step geneProductIndexingStep() {
        return stepBuilders.get(GENE_PRODUCT_INDEXING_STEP_NAME)
                .<GeneProduct, GeneProductDocument>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .listener(skipLogListener())
                .retry(HttpSolrClient.RemoteSolrException.class)
                .retryLimit(retryLimit)
                .backOffPolicy(backOffPolicy())
                .<GeneProduct>reader(geneProductMultiFileReader())
                .processor(geneProductCompositeProcessor())
                .writer(geneProductRepositoryWriter())
                .listener(logWriteRateListener())
                .listener(logStepListener())
                .build();
    }

    @Bean
    MultiResourceItemReader<GeneProduct> geneProductMultiFileReader() {
        MultiResourceItemReader<GeneProduct> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(geneProductSingleFileReader());
        return reader;
    }

    @Bean
    FlatFileItemReader<GeneProduct> geneProductSingleFileReader() {
        FlatFileItemReader<GeneProduct> reader = new FlatFileItemReader<>();
        reader.setLineMapper(geneProductLineMapper());
        reader.setLinesToSkip(headerLines);
        return reader;
    }

    @Bean
    LineMapper<GeneProduct> geneProductLineMapper() {
        DefaultLineMapper<GeneProduct> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(geneProductLineTokenizer());
        lineMapper.setFieldSetMapper(geneProductFieldSetMapper());

        return lineMapper;
    }

    @Bean
    LineTokenizer geneProductLineTokenizer() {
        return new DelimitedLineTokenizer(COLUMN_DELIMITER) {
            /**
             * Need to ignore quotes because there are entries that have single quotes in them, and this throws an
             * exception, if this method detects the quote.
             *
             * Here is an example of an offending entry:
             *
             * UniProtKB	F0Z8G9	kynu"	Kynureninase	F0Z8G9_DICPU|kynu"|DICPUDRAFT_74692	protein	taxon:5786		EMBL:GL870952|RefSeq:XP_003283711.1	db_subset=TrEMBL|taxon_name=Dictyostelium purpureum|proteome=Y|reference_proteome=UP000001064|is_isoform=N
             */
            @Override protected boolean isQuoteCharacter(char c) {
                return false;
            }
        };
    }

    @Bean
    FieldSetMapper<GeneProduct> geneProductFieldSetMapper() {
        return new StringToGeneProductMapper();
    }

    @Bean
    ItemProcessor<GeneProduct, GeneProductDocument> geneProductDocConverter() {
        return new GeneProductDocumentConverter(INTER_VALUE_DELIMITER, INTRA_VALUE_DELIMITER, SPECIFIC_VALUE_DELIMITER);
    }

    @Bean
    ItemProcessor<GeneProduct, GeneProduct> geneProductValidator() {
        Validator<GeneProduct> geneProductValidator =
                new GeneProductValidator(INTER_VALUE_DELIMITER, INTRA_VALUE_DELIMITER);
        return new ValidatingItemProcessor<>(geneProductValidator);
    }

    @Bean
    ItemProcessor<GeneProduct, GeneProductDocument> geneProductCompositeProcessor() {
        List<ItemProcessor<GeneProduct, ?>> processors = new ArrayList<>();
        processors.add(geneProductValidator());
        processors.add(geneProductDocConverter());

        CompositeItemProcessor<GeneProduct, GeneProductDocument> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(processors);

        return compositeProcessor;
    }

    @Bean
    ItemWriter<GeneProductDocument> geneProductRepositoryWriter() {
        return new SolrServerWriter<>(geneProductTemplate.getSolrClient());
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private SkipListener<GeneProduct, GeneProductDocument> skipLogListener() {
        return new SkipLoggerListener<>();
    }

    private BackOffPolicy backOffPolicy() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialBackOffInterval);
        backOffPolicy.setMaxInterval(maxBackOffInterval);
        return backOffPolicy;
    }

    private ItemWriteListener<QuickGODocument> logWriteRateListener() {
        return new ItemRateWriterListener<>(Instant.now());
    }

    private StepExecutionListener logStepListener() {
        return new LogStepListener();
    }
}
