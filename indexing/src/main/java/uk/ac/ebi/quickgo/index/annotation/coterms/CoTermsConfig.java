package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class CoTermsConfig {

    public static final String ELECTRONIC = "IEA";
    public static final Predicate<AnnotationDocument>
            EXCLUDE_ANNOTATIONS_PRODUCED_BY_ELECTRONIC_MEANS = annotationDocument -> !ELECTRONIC.equals(annotationDocument.goEvidence);
    public static final Predicate<AnnotationDocument> INCLUDE_ALL_ANNOTATIONS = annotationDocument -> true;
    private final Logger LOGGER = LoggerFactory.getLogger(CoTermsConfig.class);

    public static final String COTERM_MANUAL_COMPLETION_STEP_NAME = "coTermManualSummarizationStep";
    public static final String COTERM_ALL_COMPLETION_STEP_NAME = "cosTermAllSummarizationStep";
    private static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    private static final String DELIMITER = "\t";

    @Value("${indexing.coterms.manual}")
    private Resource manual;

    @Value("${indexing.coterms.all}")
    private Resource all;

    @Bean
    public ItemWriter<AnnotationDocument> coTermsManualAggregationWriter() {
        return new CoTermsAggregator(EXCLUDE_ANNOTATIONS_PRODUCED_BY_ELECTRONIC_MEANS);
    }

    @Bean
    public ItemWriter<AnnotationDocument> coTermsAllAggregationWriter() {
        return new CoTermsAggregator(INCLUDE_ALL_ANNOTATIONS);
    }

    @Bean
    public StepExecutionListener coTermsEndOfAggregationListener(
            ItemWriter<AnnotationDocument> coTermsManualAggregationWriter,
            ItemWriter<AnnotationDocument> coTermsAllAggregationWriter,
            StatisticsCalculator coTermsManualCalculator,
            StatisticsCalculator coTermsAllCalculator) {
        return new CoTermsStepExecutionListener(coTermsManualAggregationWriter,
                coTermsAllAggregationWriter, coTermsManualCalculator,
                coTermsAllCalculator);

    }

    @Bean
    public StatisticsCalculator coTermsManualCalculator(ItemWriter<AnnotationDocument> coTermsManualAggregationWriter) {
        return new StatisticsCalculator(coTermsManualAggregationWriter);
    }

    @Bean
    public StatisticsCalculator coTermsAllCalculator(
            ItemWriter<AnnotationDocument> coTermsAllAggregationWriter) {
        return new StatisticsCalculator(coTermsAllAggregationWriter);
    }

    @Bean
    public ItemReader<String> coTermsManualReader(
            ItemWriter<AnnotationDocument> coTermsManualAggregationWriter) {
        return new CoTermItemReader(coTermsManualAggregationWriter);
    }

    @Bean
    public ItemReader<String> coTermsAllReader(
            ItemWriter<AnnotationDocument> coTermsAllAggregationWriter) {
        return new CoTermItemReader(coTermsAllAggregationWriter);
    }

    @Bean
    ItemWriter<List<CoTerm>> coTermsManualStatsWriter() {
        return listItemFlatFileWriter(manual);
    }

    @Bean
    ItemWriter<List<CoTerm>> coTermsAllStatsWriter() {
        return listItemFlatFileWriter(all);
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
}
