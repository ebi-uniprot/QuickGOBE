package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.loader.LogJobListener;
import uk.ac.ebi.quickgo.annotation.validation.loader.LogStepListener;
import uk.ac.ebi.quickgo.annotation.validation.loader.SkipLoggerListener;
import uk.ac.ebi.quickgo.annotation.validation.loader.StringToDbXrefEntityMapper;
import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
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
 * The configuration required to validate restful requests against the annotation service, including load data
 * required for validation.
 *
 * @author Tony Wardell
 * Date: 07/11/2016
 * Time: 17:22
 * Created with IntelliJ IDEA.
 */
@Configuration
@EnableBatchProcessing
public class ValidationConfig {

    static final String LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME =
            "Load Annotation Filtering Validation Values";
    private static final String LOAD_ANNOTATION_DBXREF_ENTITIES_STEP_NAME =
            "Load Annotation DB Xref Entities Validation Values";
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationConfig.class);
    private static final String TAB = "\t";
    private static final int HEADER_LINES = 1;
    @Value("${annotation.validation.source}")
    private Resource[] resources;

    private int chunkSize = 30;
    private int skipLimit = 5;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilders;

    private static <T> FlatFileItemReader<T> validationReader(Resource resource, FlatFileItemReader<T> reader) {
        try {
            reader.setResource(new GZIPResource(resource));
        } catch (IOException e) {
            LOGGER.error(
                    "Failed to load " + Stream.of(resource) + ". " +
                            "No corresponding information for this annotation validation will be available.", e);
        }

        return reader;
    }

    @Bean
    public Job validationJob() {
        JobBuilder jobBuilder = jobBuilderFactory.get(LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME);
        return jobBuilder.start(loadDbXrefEntities())
                .listener(logJobListener())
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
    DBXRefEntityValidation dBXRefEntityValidation() {
        return new DBXRefEntityValidation();
    }

    private Step loadDbXrefEntities() {
        return stepBuilders.get(LOAD_ANNOTATION_DBXREF_ENTITIES_STEP_NAME)
                .<DBXRefEntity, DBXRefEntity>chunk(chunkSize)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(FlatFileParseException.class)
                .<DBXRefEntity>reader(validationReader(resources[0], dbXrefReader()))
                .<DBXRefEntity>writer(entityItemWriter())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
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

    private static class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }
}
