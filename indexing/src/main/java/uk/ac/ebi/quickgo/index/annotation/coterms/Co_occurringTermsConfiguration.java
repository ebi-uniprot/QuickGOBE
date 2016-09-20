package uk.ac.ebi.quickgo.index.annotation.coterms;

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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

/**
 * @author Tony Wardell
 * Date: 08/09/2016
 * Time: 11:07
 * Created with IntelliJ IDEA.
 */
@Configuration
public class Co_occurringTermsConfiguration {

    Logger LOGGER = LoggerFactory.getLogger(Co_occurringTermsConfiguration.class);

    public static final String COSTATS_MANUAL_COMPLETION_STEP_NAME = "costatsManualSummarizationStep";
    public static final String COSTATS_ALL_COMPLETION_STEP_NAME = "costatsAllSummarizationStep";
    private static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    private static final String DELIMITER = "\t";
    @Value("${indexing.coterms.dir}")
    private String path;

    @Bean
    public AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsManual() {
        return new AnnotationCo_occurringTermsAggregator(t -> !"IEA".equals(t.evidenceCode));
    }

    @Bean
    public AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsAll() {
        return new AnnotationCo_occurringTermsAggregator(t -> true);
    }

    @Bean
    public StepExecutionListener coTermsEndOfAggregationListener(
            AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsManual,
            AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsAll,
            Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorManual,
            Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorAll) {
        return new Co_occurringTermsStepExecutionListener(co_occurringGoTermsFromAnnotationsManual,
                co_occurringGoTermsFromAnnotationsAll, co_occurringTermsStatsCalculatorManual,
                co_occurringTermsStatsCalculatorAll);

    }

    @Bean
    public Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorManual(
            AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsManual) {
        return new Co_occurringTermsStatsCalculator(co_occurringGoTermsFromAnnotationsManual);
    }

    @Bean
    public Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorAll(
            AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsAll) {
        return new Co_occurringTermsStatsCalculator(co_occurringGoTermsFromAnnotationsAll);
    }

    @Bean
    public ItemReader<String> coStatsManualItemReader(AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsManual) {
        return new Co_occurringTermItemReader(co_occurringGoTermsFromAnnotationsManual);
    }

    @Bean
    public ItemReader<String> coStatsAllItemReader(AnnotationCo_occurringTermsAggregator co_occurringGoTermsFromAnnotationsAll) {
        return new Co_occurringTermItemReader(co_occurringGoTermsFromAnnotationsAll);
    }

    @Bean
    ItemWriter<List<Co_occurringTerm>> coStatManualFlatFileWriter() {
        return listItemFlatFileWriter("CoStatsManual");
    }

    @Bean
    ItemWriter<List<Co_occurringTerm>> coStatsAllFlatFileWriter() {
        return listItemFlatFileWriter("CoStatsAll");
    }

    private File getFilePath() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    private ListItemWriter<Co_occurringTerm> listItemFlatFileWriter(String fileName) {
        ListItemWriter<Co_occurringTerm> listWriter = new ListItemWriter<>(flatFileWriter(fileName));
        listWriter.setLineAggregator(new PassThroughLineAggregator<>()); //this shouldn't do anything
        return listWriter;
    }

    private FlatFileItemWriter<Co_occurringTerm> flatFileWriter(String fileName) {
        FlatFileItemWriter<Co_occurringTerm> ffw = new FlatFileItemWriter<>();
        ffw.setLineAggregator(lineAggregator());
        final File filePath = getFilePath();
        LOGGER.info("Write out co-occurring terms to {}", filePath.toString());
        Resource outputFile = new FileSystemResource(new File(filePath, fileName));
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
