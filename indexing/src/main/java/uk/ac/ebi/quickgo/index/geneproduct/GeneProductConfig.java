package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.index.common.SolrCrudRepoWriter;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepoConfig;
import uk.ac.ebi.quickgo.index.common.listener.SkipLoggerListener;

import java.util.Arrays;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepListener;
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

    @Autowired
    private GeneProductRepository repository;

    @Bean
    public Job geneProductJob() {
        return jobBuilders.get(GENE_PRODUCT_INDEXING_JOB_NAME)
                .start(geneProductIndexingStep())
                .listener(logJobListener())
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
                .<GeneProduct>reader(geneProductMultiFileReader())
                .processor(geneProductCompositeProcessor(geneProductValidator(), geneProductDocConverter()))
                .writer(geneProductRepositoryWriter())
                .listener(logStepListener())
                .listener(skipLogListener())
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
        return new DelimitedLineTokenizer(COLUMN_DELIMITER);
    }

    @Bean
    FieldSetMapper<GeneProduct> geneProductFieldSetMapper() {
        return new StringToGeneProductMapper();
    }

    @Bean
    ItemProcessor<GeneProduct, GeneProductDocument> geneProductDocConverter() {
        return new GeneProductDocumentConverter(INTER_VALUE_DELIMITER, INTRA_VALUE_DELIMITER);
    }

    @Bean
    ItemProcessor<GeneProduct, GeneProduct> geneProductValidator() {
        Validator<GeneProduct> geneProductValidator =
                new GeneProductValidator(INTER_VALUE_DELIMITER, INTRA_VALUE_DELIMITER);
        return new ValidatingItemProcessor<>(geneProductValidator);
    }

    @Bean
    ItemProcessor<GeneProduct, GeneProductDocument> geneProductCompositeProcessor(ItemProcessor<GeneProduct, ?>...
            processors) {
        CompositeItemProcessor<GeneProduct, GeneProductDocument> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(Arrays.asList(processors));

        return compositeProcessor;
    }

    @Bean
    ItemWriter<GeneProductDocument> geneProductRepositoryWriter() {
        return new SolrCrudRepoWriter<>(repository);
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private StepListener logStepListener() {
        return new LogStepListener();
    }

    private StepListener skipLogListener() {
        return new SkipLoggerListener();
    }
}