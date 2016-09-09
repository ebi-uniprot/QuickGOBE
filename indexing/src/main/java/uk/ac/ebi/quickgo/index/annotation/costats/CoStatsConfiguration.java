package uk.ac.ebi.quickgo.index.annotation.costats;

import uk.ac.ebi.quickgo.common.costats.CoOccurringTerm;
import uk.ac.ebi.quickgo.index.annotation.Annotation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
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
public class CoStatsConfiguration {

    public static final String[] FF_COL_NAMES = {"target", "comparedTerm", "probabilityRatio", "similarityRatio",
            "together", "compared"};
    public static final String DELIMITER = "\t";
    @Value("${indexing.annotation.source}")
    private Resource[] resources;

    public static final String COSTATS_MANUAL_COMPLETION_STEP_NAME = "costatsManualSummarizationStep";
    public static final String COSTATS_ALL_COMPLETION_STEP_NAME = "costatsAllSummarizationStep";

    private CoStatsPermutations coStatsPermutationsMan;
    private CoStatsPermutations coStatsPermutationsAll;

    @Bean
    ItemProcessor<Annotation, Annotation> coStatsManual(){
        coStatsPermutationsMan = new CoStatsPermutations();
        Predicate<Annotation> toBeProcessed = t -> !"IEA".equals(t.evidenceCode);
        return new AnnotationCoStatsProcessor(coStatsPermutationsMan, toBeProcessed);
    }


    @Bean
    ItemProcessor<Annotation, Annotation> coStatsAll(){
        coStatsPermutationsAll = new CoStatsPermutations();
        Predicate<Annotation> toBeProcessed = t -> true;
        return new AnnotationCoStatsProcessor(coStatsPermutationsAll, toBeProcessed);
    }


    @Bean
    ItemProcessor<String, List<CoOccurringTerm>> coStatsManualItemProcessor(){
        CoStatsItemProcessor coStatsItemProcessor = new CoStatsItemProcessor(
                coStatsPermutationsMan.totalOfAnnotatedGeneProducts(),
                coStatsPermutationsMan.termGPCount(),
                coStatsPermutationsMan.termToTermOverlapMatrix());

        return coStatsItemProcessor;
    }


    @Bean
    ItemProcessor<String, List<CoOccurringTerm>> coStatsAllItemProcessor(){
        CoStatsItemProcessor coStatsItemProcessor = new CoStatsItemProcessor(
                coStatsPermutationsAll.totalOfAnnotatedGeneProducts(),
                coStatsPermutationsAll.termGPCount(),
                coStatsPermutationsAll.termToTermOverlapMatrix());

        return coStatsItemProcessor;
    }

    @Bean
    ItemReader<String> coStatsManualItemReader(){
        return new CoStatsForTermItemReader(coStatsPermutationsMan);
    }

    @Bean
    ItemReader<String> coStatsAllItemReader(){
        return new CoStatsForTermItemReader(coStatsPermutationsAll);
    }


    @Bean
    ItemWriter<List<CoOccurringTerm>> coStatManualFlatFileWriter() {
        return listItemFlatFileWriter("CoStatsManual" );
    }

    @Bean
    ItemWriter<List<CoOccurringTerm>> coStatsAllFlatFileWriter() {
        return listItemFlatFileWriter("CoStatsAll" );
    }


    public String getFilePath() {
        String path;
        try {
            path = resources[0].getFile().getPath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create path for Costats", e);
        }
        return path;
    }

    private ItemWriter<List<CoOccurringTerm>> listItemFlatFileWriter(String fileName) {
        ItemWriter<List<CoOccurringTerm>> listWriter = new ListItemWriter<>(flatFileWriter(fileName));
        return listWriter;
    }

    private ItemWriter<CoOccurringTerm> flatFileWriter(String fileName) {FlatFileItemWriter<CoOccurringTerm> ffw = new FlatFileItemWriter<>();
        ffw.setLineAggregator(lineAggregator());
        File file = new File(getFilePath(), fileName );
        Resource outputFile = new FileSystemResource(file);
        ffw.setResource(outputFile);
        return ffw;
    }

    private FieldExtractor<CoOccurringTerm> beanWrapperFieldExtractor(){
        BeanWrapperFieldExtractor<CoOccurringTerm> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor();
        beanWrapperFieldExtractor.setNames(FF_COL_NAMES);
        return beanWrapperFieldExtractor;
    }

    private LineAggregator<CoOccurringTerm> lineAggregator(){
        DelimitedLineAggregator<CoOccurringTerm> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(DELIMITER);
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor());
        return delimitedLineAggregator;
    }
}
