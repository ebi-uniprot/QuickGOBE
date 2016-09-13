package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.index.annotation.Annotation;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author Tony Wardell
 * Date: 08/09/2016
 * Time: 11:07
 * Created with IntelliJ IDEA.
 */
@Configuration
public class Co_occurringTermConfiguration {

    private static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    private static final String DELIMITER = "\t";

    @Value("${indexing.coterms.dir}")
    private String path;

    public static final String COSTATS_MANUAL_COMPLETION_STEP_NAME = "costatsManualSummarizationStep";
    public static final String COSTATS_ALL_COMPLETION_STEP_NAME = "costatsAllSummarizationStep";

    @Bean
    AnnotationCo_occurringTermsAggregator annotationCoTermsAggregatorMan() {
        return new AnnotationCo_occurringTermsAggregator();
    }

    @Bean
    AnnotationCo_occurringTermsAggregator annotationCoTermsAggregatorAll() {
        return new AnnotationCo_occurringTermsAggregator();
    }

    @Bean
    public ItemProcessor<Annotation, Annotation> coOccuringGoTermsFromAnnotationsManual(
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorMan) {
        Predicate<Annotation> toBeProcessed = t -> !"IEA".equals(t.evidenceCode);
        return new Co_occurringGoTermsFromAnnotations(annotationCoOccurringTermsAggregatorMan, toBeProcessed);
    }

    @Bean
    public ItemProcessor<Annotation, Annotation> coOccuringGoTermsFromAnnotationsAll(
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorAll) {
        Predicate<Annotation> toBeProcessed = t -> true;
        return new Co_occurringGoTermsFromAnnotations(annotationCoOccurringTermsAggregatorAll, toBeProcessed);
    }

    @Bean
    public Co_occurringTermsStepExecutionListener coTermsStepExecutionListener(
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorAll,
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorMan) {
        return new Co_occurringTermsStepExecutionListener(annotationCoOccurringTermsAggregatorAll,
                annotationCoOccurringTermsAggregatorMan);

    }

    @Bean
    public static ItemProcessor<String, List<Co_occurringTerm>> coOccurringTermsStatsCalculatorManual(
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorMan) {
        return new Co_occurringTermsStatsCalculator(annotationCoOccurringTermsAggregatorMan);
    }

    @Bean
    public static ItemProcessor<String, List<Co_occurringTerm>> coOccurringTermsStatsCalculatorAll(
            AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorAll) {
        return new Co_occurringTermsStatsCalculator(annotationCoOccurringTermsAggregatorAll);
    }

    @Bean
    public ItemReader<String> coStatsManualItemReader(AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorMan) {
        return new Co_occurringTermItemReader(annotationCoOccurringTermsAggregatorMan);
    }

    @Bean
    public ItemReader<String> coStatsAllItemReader(AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregatorAll) {
        return new Co_occurringTermItemReader(annotationCoOccurringTermsAggregatorAll);
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
        Resource outputFile = new FileSystemResource(new File(getFilePath(), fileName));
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
