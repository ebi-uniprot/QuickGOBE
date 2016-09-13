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
public class CoTermConfiguration {

    public static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    public static final String DELIMITER = "\t";

    @Value("${indexing.coterms.dir}")
    private String path;

    public static final String COSTATS_MANUAL_COMPLETION_STEP_NAME = "costatsManualSummarizationStep";
    public static final String COSTATS_ALL_COMPLETION_STEP_NAME = "costatsAllSummarizationStep";

    @Bean
    AnnotationCoTermsAggregator annotationCoTermsAggregatorMan(){
        return new AnnotationCoTermsAggregator();
    }

    @Bean
    AnnotationCoTermsAggregator annotationCoTermsAggregatorAll(){
        return new AnnotationCoTermsAggregator();
    }

    @Bean
    public ItemProcessor<Annotation, Annotation> coOccuringGoTermsFromAnnotationsManual(AnnotationCoTermsAggregator annotationCoTermsAggregatorMan){
        Predicate<Annotation> toBeProcessed = t -> !"IEA".equals(t.evidenceCode);
        return new CoOccuringGoTermsFromAnnotations(annotationCoTermsAggregatorMan, toBeProcessed);
    }

    @Bean
    public ItemProcessor<Annotation, Annotation> coOccuringGoTermsFromAnnotationsAll(AnnotationCoTermsAggregator annotationCoTermsAggregatorAll){
        Predicate<Annotation> toBeProcessed = t -> true;
        return new CoOccuringGoTermsFromAnnotations(annotationCoTermsAggregatorAll, toBeProcessed);
    }

    @Bean
    public CoTermsStepExecutionListener coTermsStepExecutionListener(AnnotationCoTermsAggregator annotationCoTermsAggregatorAll,
            AnnotationCoTermsAggregator annotationCoTermsAggregatorMan){
        return new CoTermsStepExecutionListener(annotationCoTermsAggregatorAll, annotationCoTermsAggregatorMan);

    }

    @Bean
    public static ItemProcessor<String, List<CoOccurringTerm>> coOccurringTermsStatsCalculatorManual(AnnotationCoTermsAggregator annotationCoTermsAggregatorMan){
        CoOccurringTermsStatsCalculator coOccurringTermsStatsCalculator = new CoOccurringTermsStatsCalculator(annotationCoTermsAggregatorMan);
        return coOccurringTermsStatsCalculator;
    }

    @Bean
    public static ItemProcessor<String, List<CoOccurringTerm>> coOccurringTermsStatsCalculatorAll(AnnotationCoTermsAggregator annotationCoTermsAggregatorAll){
        CoOccurringTermsStatsCalculator coOccurringTermsStatsCalculator = new CoOccurringTermsStatsCalculator(annotationCoTermsAggregatorAll);
        return coOccurringTermsStatsCalculator;
    }

    @Bean
    public ItemReader<String> coStatsManualItemReader(AnnotationCoTermsAggregator annotationCoTermsAggregatorMan){
        return new CoStatsForTermItemReader(annotationCoTermsAggregatorMan);
    }

    @Bean
    public ItemReader<String> coStatsAllItemReader(AnnotationCoTermsAggregator annotationCoTermsAggregatorAll){
        return new CoStatsForTermItemReader(annotationCoTermsAggregatorAll);
    }


    @Bean
    ItemWriter<List<CoOccurringTerm>> coStatManualFlatFileWriter() {
        return listItemFlatFileWriter("CoStatsManual" );
    }

    @Bean
    ItemWriter<List<CoOccurringTerm>> coStatsAllFlatFileWriter() {
        return listItemFlatFileWriter("CoStatsAll" );
    }


    public File getFilePath() {
        File file = new File(path);
            if(!file.exists()){
                file.mkdir();
            }
            return file;
    }

    private ListItemWriter<CoOccurringTerm> listItemFlatFileWriter(String fileName) {
        ListItemWriter<CoOccurringTerm> listWriter = new ListItemWriter<>(flatFileWriter(fileName));
        listWriter.setLineAggregator(new PassThroughLineAggregator<>()); //this shouldn't do anything
        return listWriter;
    }

    private FlatFileItemWriter<CoOccurringTerm> flatFileWriter(String fileName) {
        FlatFileItemWriter<CoOccurringTerm> ffw = new FlatFileItemWriter<>();
        ffw.setLineAggregator(lineAggregator());
        Resource outputFile = new FileSystemResource(new File(getFilePath(), fileName ));
        ffw.setResource(outputFile);
        FlatFileHeaderCallback headerCallBack = new CoTermsFlatFileHeaderCallBack();
        ffw.setHeaderCallback(headerCallBack);
        return ffw;
    }

    private LineAggregator<CoOccurringTerm> lineAggregator(){
        DelimitedLineAggregator<CoOccurringTerm> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(DELIMITER);
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor());
        return delimitedLineAggregator;
    }

    private BeanWrapperFieldExtractor<CoOccurringTerm> beanWrapperFieldExtractor(){
        BeanWrapperFieldExtractor<CoOccurringTerm> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        beanWrapperFieldExtractor.setNames(FF_COL_NAMES);
        return beanWrapperFieldExtractor;
    }
}
