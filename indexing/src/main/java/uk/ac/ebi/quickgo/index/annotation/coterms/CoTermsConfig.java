package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.writer.ListItemWriter;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Bean
    public OutputPath outputPath(@Value("${indexing.coterms.manual:#{systemProperties['user" +
            ".dir']}/QuickGO/CoTermsManual}") String manualCoTermsPath,
            @Value("${indexing.coterms.all:#{systemProperties['user.dir']}/QuickGO/CoTermsAll}") String allCoTermsPath){
            return new OutputPath(manualCoTermsPath, allCoTermsPath);
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

    @Bean
    public ItemReader<String> coTermsManualReader(
            CoTermsAggregationWriter coTermsManualAggregationWriter) {
        return new CoTermItemReader(coTermsManualAggregationWriter);
    }

    @Bean
    public ItemReader<String> coTermsAllReader(
            CoTermsAggregationWriter coTermsAllAggregationWriter) {
        return new CoTermItemReader(coTermsAllAggregationWriter);
    }

    @Bean
    ItemWriter<List<CoTerm>> coTermsManualStatsWriter(OutputPath outputPath) {
        return listItemFlatFileWriter(outputPath.manualCoTermsResource);
    }

    @Bean
    ItemWriter<List<CoTerm>> coTermsAllStatsWriter(OutputPath outputPath) {
        return listItemFlatFileWriter(outputPath.allCoTermsResource);
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

    public class OutputPath {
        FileSystemResource manualCoTermsResource;
        FileSystemResource allCoTermsResource;
        public OutputPath(String manualCoTermsPath, String allCoTermsPath) {
            Preconditions.checkArgument(Objects.nonNull(manualCoTermsPath), "The output path for the 'manual' coterms" +
                    " file cannot be null");
            Preconditions.checkArgument(Objects.nonNull(allCoTermsPath), "The output path for the 'all' coterms" +
                    " file cannot be null");
            Preconditions.checkArgument(!allCoTermsPath.equals(manualCoTermsPath), "The output path for manual and all " +
                    "coterms files should not be the same, but they were both %s", manualCoTermsPath);

            manualCoTermsResource = new FileSystemResource(manualCoTermsPath);
            allCoTermsResource =  new FileSystemResource(allCoTermsPath);

        }
    }
}
