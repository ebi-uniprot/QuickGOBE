package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.common.QuickGODocument;
import uk.ac.ebi.quickgo.index.common.listener.ItemRateWriterListener;
import uk.ac.ebi.quickgo.index.common.listener.LogStepListener;
import uk.ac.ebi.quickgo.index.common.listener.SkipLoggerListener;
import uk.ac.ebi.quickgo.index.common.writer.ListItemWriter;

import com.google.common.base.Preconditions;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;

/**
 *
 * Spring Configuration class for Co-occurring terms.
 *
 * @author Tony Wardell
 * Date: 08/09/2016
 * Time: 11:07
 * Created with IntelliJ IDEA.
 */
@Configuration
@EnableBatchProcessing
public class CoTermsConfig {

    private static final String ELECTRONIC = "IEA";
    private static final Predicate<AnnotationDocument>
            EXCLUDE_ANNOTATIONS_PRODUCED_BY_ELECTRONIC_MEANS =
            annotationDocument -> !ELECTRONIC.equals(annotationDocument.goEvidence);
    private static final Predicate<AnnotationDocument> INCLUDE_ALL_ANNOTATIONS = annotationDocument -> true;
    private final Logger LOGGER = LoggerFactory.getLogger(CoTermsConfig.class);

    public static final String CO_TERM_MANUAL_SUMMARIZATION_STEP = "coTermManualSummarizationStep";
    public static final String CO_TERM_ALL_SUMMARIZATION_STEP = "coTermAllSummarizationStep";
    private static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    private static final String DELIMITER = "\t";

    @Value("${indexing.coterms.chunk.size:1}")
    private int cotermsChunk;
    @Value("${indexing.coterm.loginterval:1000}")
    private int coTermLogInterval;
    @Value("${indexing.coterms.manual:#{systemProperties['user.dir']}/QuickGO/CoTermsManual}")
    Resource manualCoTermsPath;
    @Value("${indexing.coterms.all:#{systemProperties['user.dir']}/QuickGO/CoTermsAll}")
    Resource allCoTermsPath;

    @Autowired
    public StepBuilderFactory stepBuilders;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Step coTermManualSummarizationStep() {
        Preconditions.checkState(!allCoTermsPath.equals(manualCoTermsPath), "The output path for manual and all " +
                "coterms files should not be the same, but they were both %s", manualCoTermsPath);

        return stepBuilders.get(CO_TERM_MANUAL_SUMMARIZATION_STEP)
                .<String, List<CoTerm>>chunk(cotermsChunk)
                .reader(coTermsManualReader(coTermsManualAggregationWriter()))
                .processor(coTermsManualCalculator(coTermsManualAggregationWriter()))
                .writer(coTermsManualStatsWriter(manualCoTermsPath))
                .listener(logStepListener())
                .listener(logWriteRateListener(coTermLogInterval))
                .listener(skipLogListener())
                .build();
    }

    @Bean
    public Step coTermAllSummarizationStep() {
        Preconditions.checkState(!allCoTermsPath.equals(manualCoTermsPath), "The output path for manual and all " +
                "coterms files should not be the same, but they were both %s", manualCoTermsPath);

        return stepBuilders.get(CO_TERM_ALL_SUMMARIZATION_STEP)
                .<String, List<CoTerm>>chunk(cotermsChunk)
                .reader(coTermsAllReader(coTermsAllAggregationWriter()))
                .processor(coTermsAllCalculator(coTermsAllAggregationWriter()))
                .writer(coTermsAllStatsWriter(allCoTermsPath))
                .listener(logStepListener())
                .listener(logWriteRateListener(coTermLogInterval))
                .listener(skipLogListener())
                .build();
    }

    @Bean
    public CoTermsAggregationWriter coTermsManualAggregationWriter() {
        return new CoTermsAggregationWriter(EXCLUDE_ANNOTATIONS_PRODUCED_BY_ELECTRONIC_MEANS);
    }

    @Bean
    public CoTermsAggregationWriter coTermsAllAggregationWriter() {
        return new CoTermsAggregationWriter(INCLUDE_ALL_ANNOTATIONS);
    }

    @Bean
    public CoTermsProcessor coTermsManualCalculator(CoTermsAggregationWriter coTermsManualAggregationWriter) {
        return new CoTermsProcessor(coTermsManualAggregationWriter);
    }

    @Bean
    public CoTermsProcessor coTermsAllCalculator(
            CoTermsAggregationWriter coTermsAllAggregationWriter) {
        return new CoTermsProcessor(coTermsAllAggregationWriter);
    }

    private ItemReader<String> coTermsManualReader(
            CoTermsAggregationWriter coTermsManualAggregationWriter) {
        return new CoTermItemReader(coTermsManualAggregationWriter);
    }

    private ItemReader<String> coTermsAllReader(
            CoTermsAggregationWriter coTermsAllAggregationWriter) {
        return new CoTermItemReader(coTermsAllAggregationWriter);
    }

    private ItemWriter<List<CoTerm>> coTermsManualStatsWriter(Resource outputPath) {
        Preconditions.checkArgument(Objects.nonNull(outputPath), "The output path for the 'manual' coterms" +
                " file cannot be null");
        return listItemFlatFileWriter(outputPath);
    }

    private ItemWriter<List<CoTerm>> coTermsAllStatsWriter(Resource outputPath) {
        Preconditions.checkArgument(Objects.nonNull(outputPath), "The output path for the 'all' coterms" +
                " file cannot be null");
        return listItemFlatFileWriter(outputPath);
    }

    private ListItemWriter<CoTerm> listItemFlatFileWriter(Resource outputFile) {
        ListItemWriter<CoTerm> listWriter = new ListItemWriter<>(flatFileWriter(outputFile));
        listWriter.setLineAggregator(new PassThroughLineAggregator<>());
        return listWriter;
    }

    private FlatFileItemWriter<CoTerm> flatFileWriter(Resource outputFile) {
        FlatFileItemWriter<CoTerm> ffw = new FlatFileItemWriter<>();
        ffw.setLineAggregator(lineAggregator());
        LOGGER.info("Write out co-occurring terms to {}", outputFile.toString());
        ffw.setResource(outputFile);
        FlatFileHeaderCallback headerCallBack = new CoTermsFlatFileHeaderCallBack();
        ffw.setHeaderCallback(headerCallBack);
        return ffw;
    }

    private LineAggregator<CoTerm> lineAggregator() {
        DelimitedLineAggregator<CoTerm> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(DELIMITER);
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor());
        return delimitedLineAggregator;
    }

    private BeanWrapperFieldExtractor<CoTerm> beanWrapperFieldExtractor() {
        BeanWrapperFieldExtractor<CoTerm> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(FF_COL_NAMES);
        return beanWrapperFieldExtractor;
    }


    private ItemWriteListener<QuickGODocument> logWriteRateListener(final int writeInterval) {
        return new ItemRateWriterListener<>(Instant.now(), writeInterval);
    }

    private StepExecutionListener logStepListener() {
        return new LogStepListener();
    }

    //todo is this signature correct?
    private SkipLoggerListener<AnnotationDocument, AnnotationDocument> skipLogListener() {
        return new SkipLoggerListener<>();
    }

}
