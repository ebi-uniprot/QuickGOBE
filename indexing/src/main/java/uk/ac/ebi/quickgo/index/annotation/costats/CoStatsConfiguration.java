package uk.ac.ebi.quickgo.index.annotation.costats;

import uk.ac.ebi.quickgo.common.costats.CoOccurrenceStat;
import uk.ac.ebi.quickgo.index.annotation.Annotation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
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
    ItemProcessor<String, List<CoOccurrenceStat>> coStatsManualItemProcessor(){
        CoStatsItemProcessor coStatsItemProcessor = new CoStatsItemProcessor(
                coStatsPermutationsMan.totalOfAnnotatedGeneProducts(),
                coStatsPermutationsMan.termGPCount(),
                coStatsPermutationsMan.termToTermOverlapMatrix());

        return coStatsItemProcessor;
    }


    @Bean
    ItemProcessor<String, List<CoOccurrenceStat>> coStatsAllItemProcessor(){
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
    ItemWriter<List<CoOccurrenceStat>> coStatManualFlatFileWriter() {
        return createFlatFileWriter("CoStatsManual" );
    }

    @Bean
    ItemWriter<List<CoOccurrenceStat>> coStatsAllFlatFileWriter() {
        return createFlatFileWriter("CoStatsAll" );
    }

    private ItemWriter<List<CoOccurrenceStat>> createFlatFileWriter(String fileName) {
        FlatFileItemWriter<List<CoOccurrenceStat>> ffw = new FlatFileItemWriter<>();
        File file = new File(getFilePath(), fileName );
        Resource outputFile = new FileSystemResource(file);
        ffw.setResource(outputFile);
        return ffw;
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
}
