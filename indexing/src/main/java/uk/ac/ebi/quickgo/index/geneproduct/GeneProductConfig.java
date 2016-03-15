package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.index.common.SolrCrudRepoWriter;
import uk.ac.ebi.quickgo.index.common.listener.LogJobListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.geneproduct.common.RepoConfig;

import java.util.Arrays;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
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
@Import({RepoConfig.class})
public class GeneProductConfig {
    static final String GENE_PRODUCT_INDEXING_STEP_NAME = "geneProductIndex";

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

    @Autowired
    private GeneProductRepository repository;

    @Bean
    public Job geneProductJob() {
        return jobBuilders.get("geneProductJob")
                .listener(logJobListener())
                .flow(readGeneProductData())
                .end()
                .build();
    }

    private Step readGeneProductData() {
        return stepBuilders.get(GENE_PRODUCT_INDEXING_STEP_NAME)
                .<GeneProduct, GeneProductDocument>chunk(chunkSize)
                .<GeneProduct>reader(multiFileReader())
                .processor(compositeProcessor(gpValidator(), docConverter()))
                .writer(geneProductRepositoryWriter())
                .listener(logStepListener())
                .build();
    }

    private MultiResourceItemReader<GeneProduct> multiFileReader() {
        MultiResourceItemReader<GeneProduct> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);
        reader.setDelegate(singleFileReader());

        return reader;
    }

    private FlatFileItemReader<GeneProduct> singleFileReader() {
        FlatFileItemReader<GeneProduct> reader = new FlatFileItemReader<>();
        reader.setLineMapper(lineMapper());
        return reader;
    }

    private LineMapper<GeneProduct> lineMapper() {
        DefaultLineMapper<GeneProduct> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer());
        lineMapper.setFieldSetMapper(fieldSetMapper());

        return lineMapper;
    }

    private LineTokenizer lineTokenizer() {
        return new DelimitedLineTokenizer(COLUMN_DELIMITER);
    }

    private FieldSetMapper<GeneProduct> fieldSetMapper() {
        return new StringToGeneProductMapper();
    }

    private ItemProcessor<GeneProduct, GeneProductDocument> docConverter() {
        return new GeneProductDocumentConverter(INTER_VALUE_DELIMITER, INTRA_VALUE_DELIMITER);
    }

    private ItemProcessor<GeneProduct, GeneProduct> gpValidator() {
        Validator<GeneProduct> geneProductValidator =
                new GeneProductValidator(INTER_VALUE_DELIMITER, INTRA_VALUE_DELIMITER);
        return new ValidatingItemProcessor<>(geneProductValidator);
    }

    private ItemProcessor<GeneProduct, GeneProductDocument> compositeProcessor(ItemProcessor<GeneProduct, ?>... processors) {
        CompositeItemProcessor<GeneProduct, GeneProductDocument> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(Arrays.asList(processors));

        return compositeProcessor;
    }

    private ItemWriter<GeneProductDocument> geneProductRepositoryWriter() {
        return new SolrCrudRepoWriter<>(repository);
    }

    private LogJobListener logJobListener() {
        return new LogJobListener();
    }

    private LogStepListener logStepListener() {
        return new LogStepListener();
    }
}