package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.loader.*;
import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

/**
 * @author Tony Wardell
 * Date: 07/11/2016
 * Time: 17:22
 * Created with IntelliJ IDEA.
 */
@Configuration
@EnableBatchProcessing
public class ValidationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationConfig.class);

    public static final String LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME =
            "Load Annotation Filtering Validation Values";
    public static final String LOAD_ANNOTATION_DBXREF_ENTITIES_STEP_NAME =
            "Load Annotation DB Xref Entities Validation Values";
    private static final String TAB = "\t";

    @Value("${annotation.validation.source}")
    private Resource[] resources;

    //@Value("${annotation.validation.chunksize:30}")
    private String chunkSize="30";
    //@Value("${foo.bar.skipLimit:5}")
    private String skipLimit="5";
    private static final int HEADER_LINES = 1;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Bean
    public Job validationJob(){
        JobBuilder jobBuilder = jobBuilderFactory.get(LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME);
        return jobBuilder.start(loadDbXrefEntities())
        .listener(logJobListener())
        .build();
    }

//    @Bean
    private Step loadDbXrefEntities() {
        return stepBuilders.get(LOAD_ANNOTATION_DBXREF_ENTITIES_STEP_NAME)
                .<DBXRefEntity, DBXRefEntity>chunk(Integer.parseInt(chunkSize))
                .faultTolerant()
                .skipLimit(Integer.parseInt(skipLimit))
                .skip(FlatFileParseException.class)
                .<DBXRefEntity>reader(validationReader(resources[0], dbXrefReader()))
                .<DBXRefEntity>writer(entityItemWriter())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
    }

    @Bean
    FlatFileItemReader<DBXRefEntity> dbXrefReader() {
        FlatFileItemReader<DBXRefEntity> reader = new FlatFileItemReader<>();
        reader.setLineMapper(dbXrefEntityLineMapper());
        reader.setLinesToSkip(HEADER_LINES);
        return reader;
    }


    @Bean
    LineMapper<DBXRefEntity> dbXrefEntityLineMapper() {
        DefaultLineMapper<DBXRefEntity> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(dbXrefLineTokenizer());
        lineMapper.setFieldSetMapper(dbXrefFieldSetMapper());
        return lineMapper;
    }

    @Bean
    LineTokenizer dbXrefLineTokenizer() {
        return new DelimitedLineTokenizer(TAB);
    }

    @Bean
    FieldSetMapper<DBXRefEntity> dbXrefFieldSetMapper() {
        return new StringToDbXrefEntityMapper();
    }

    @Bean
    ItemWriter<DBXRefEntity> entityItemWriter() {
        return new DBXRefEntityValidation.DBXRefEntityAggregator();
    }

    @Bean
    DBXRefEntityValidation dBXRefEntityValidation(){
        return new DBXRefEntityValidation();
    }

    private JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    private StepExecutionListener logStepListener() {
        return new LogStepListener();
    }

    private SkipLoggerListener<DBXRefEntity, DBXRefEntity> skipLogListener() {
        return new SkipLoggerListener<>();
    }

    private static <T> FlatFileItemReader<T> validationReader(Resource resource, FlatFileItemReader<T> reader) {

        try{
            reader.setResource(new GZIPResource(resource));
        } catch (IOException e) {
            LOGGER.error(
                    "Failed to load " + Stream.of(resource) + ". " +
                            "No corresponding information for this annotation validation will be available.", e);
        }

        return reader;
    }

//    public static <T> MultiResourceItemReader<T> validationReader(
//            Resource[] resources,
//            FlatFileItemReader<T> itemReader) {
//        MultiResourceItemReader<T> reader = new MultiResourceItemReader<>();
//
//        setResourceComparator(reader);
//
//        try {
//            GZIPResource[] zippedResources = new GZIPResource[resources.length];
//            for (int i = 0; i < resources.length; i++) {
//                zippedResources[i] = new GZIPResource(resources[i]);
//            }
//
//            reader.setResources(zippedResources);
//            reader.setDelegate(itemReader);
//        } catch (IOException e) {
//            LOGGER.error(
//                    "Failed to load preset information for " + Stream.of(resources) + ". " +
//                            "No corresponding information for this preset will be available.", e);
//        }
//
//        return reader;
//    }

    private static class GZIPResource extends InputStreamResource implements Resource {
                GZIPResource(Resource delegate) throws IOException {
                    super(new GZIPInputStream(delegate.getInputStream()));
                }
            }

    private static <T> void setResourceComparator(MultiResourceItemReader<T> reader) {
        reader.setComparator((o1, o2) -> 0);
    }

//    @Bean
//    FlatFileItemReader<DBXRefEntity> ffr(){
//        FlatFileItemReader ffr = new FlatFileItemReader<>();
//        ffr.setBufferedReaderFactory(gzipBufferedReaderFactory());
//        return ffr;
//    }
//
//    private BufferedReaderFactory gzipBufferedReaderFactory() {
//        return null;
//    }
//
//    private class GzippedBufferedReaderFactory implements BufferedReaderFactory{
//
//        @Override public BufferedReader create(Resource resource, String encoding)
//                throws UnsupportedEncodingException, IOException {
//            return new GzippedBufferedReader();
//        }
//    }
//
//    private class GzippedBufferedReader extends BufferedReader {
//
//        public GzippedBufferedReader(Reader in, int sz) {
//            super(in, sz);
//        }
//
//        public GzippedBufferedReader(Reader in) {
//            super(in);
//        }
//    }
}
