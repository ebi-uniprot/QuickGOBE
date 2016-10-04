package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import java.util.List;
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
public class Co_occurringTermsConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(Co_occurringTermsConfig.class);

    public static final String COSTATS_MANUAL_COMPLETION_STEP_NAME = "costatsManualSummarizationStep";
    public static final String COSTATS_ALL_COMPLETION_STEP_NAME = "costatsAllSummarizationStep";
    private static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    private static final String DELIMITER = "\t";

    @Value("${indexing.coterms.manual}")
    private Resource manual;

    @Value("${indexing.coterms.all}")
    private Resource all;

    @Bean
    public ItemWriter<AnnotationDocument> coTermsManualAggregationWriter() {
        return new AnnotationCoOccurringTermsAggregator(t -> !"IEA".equals(t.goEvidence));
    }

    @Bean
    public ItemWriter<AnnotationDocument> coTermsAllAggregationWriter() {
        return new AnnotationCoOccurringTermsAggregator(t -> true);
    }

    @Bean
    public StepExecutionListener coTermsEndOfAggregationListener(
            ItemWriter<AnnotationDocument> coTermsManualAggregationWriter,
            ItemWriter<AnnotationDocument> coTermsAllAggregationWriter,
            CoTermsStatsCalculator coTermsManualCalculator,
            CoTermsStatsCalculator coTermsAllCalculator) {
        return new Co_occurringTermsStepExecutionListener(coTermsManualAggregationWriter,
                coTermsAllAggregationWriter, coTermsManualCalculator,
                coTermsAllCalculator);

    }

    @Bean
    public CoTermsStatsCalculator coTermsManualCalculator(
            ItemWriter<AnnotationDocument> coTermsManualAggregationWriter) {
        return new CoTermsStatsCalculator(coTermsManualAggregationWriter);
    }

    @Bean
    public CoTermsStatsCalculator coTermsAllCalculator(
            ItemWriter<AnnotationDocument> coTermsAllAggregationWriter) {
        return new CoTermsStatsCalculator(coTermsAllAggregationWriter);
    }

    @Bean
    public ItemReader<String> coTermsManualReader(
            ItemWriter<AnnotationDocument> coTermsManualAggregationWriter) {
        return new Co_occurringTermItemReader(coTermsManualAggregationWriter);
    }

    @Bean
    public ItemReader<String> coTermsAllReader(
            ItemWriter<AnnotationDocument> coTermsAllAggregationWriter) {
        return new Co_occurringTermItemReader(coTermsAllAggregationWriter);
    }

    @Bean
    ItemWriter<List<Co_occurringTerm>> coTermsManualStatsWriter() {
        return listItemFlatFileWriter(manual);
    }

    @Bean
    ItemWriter<List<Co_occurringTerm>> coTermsAllStatsWriter() {
        return listItemFlatFileWriter(all);
    }

    private ListItemWriter<Co_occurringTerm> listItemFlatFileWriter(Resource outputFile) {
        ListItemWriter<Co_occurringTerm> listWriter = new ListItemWriter<>(flatFileWriter(outputFile));
        listWriter.setLineAggregator(new PassThroughLineAggregator<>()); //this shouldn't do anything
        return listWriter;
    }

    private FlatFileItemWriter<Co_occurringTerm> flatFileWriter(Resource outputFile) {
        FlatFileItemWriter<Co_occurringTerm> ffw = new FlatFileItemWriter<>();
        ffw.setLineAggregator(lineAggregator());
        LOGGER.info("Write out co-occurring terms to {}", outputFile.toString());
        ffw.setResource(outputFile);
        FlatFileHeaderCallback headerCallBack = new Co_occurringTermsFlatFileHeaderCallBack();
        ffw.setHeaderCallback(headerCallBack);
        return ffw;
    }

    private LineAggregator<Co_occurringTerm> lineAggregator() {
        DelimitedLineAggregator<Co_occurringTerm> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(DELIMITER);
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor());
        return delimitedLineAggregator;
    }

    private BeanWrapperFieldExtractor<Co_occurringTerm> beanWrapperFieldExtractor() {
        BeanWrapperFieldExtractor<Co_occurringTerm> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(FF_COL_NAMES);
        return beanWrapperFieldExtractor;
    }
}
