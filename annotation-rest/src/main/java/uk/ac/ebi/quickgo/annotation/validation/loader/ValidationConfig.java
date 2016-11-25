package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.*;
import uk.ac.ebi.quickgo.annotation.validation.service.ValidationEntityChecker;

import java.io.IOException;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
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
@EnableConfigurationProperties(ValidationLoadProperties.class)
public class ValidationConfig {

    public static final String LOAD_ANNOTATION_DBXREF_ENTITIES_STEP_NAME =
            "Load Annotation DB Xref Entities Validation Values";
    private static final String LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME = "validationLoadingJob";
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationConfig.class);
    private static final String TAB = "\t";
    private static final int HEADER_LINES = 1;

    @Autowired
    private ValidationLoadProperties validationLoadProperties;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Bean
    public Job validationJob(Step validationEntitiesStep) {
        return jobBuilders.get(LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME)
                .start(validationEntitiesStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    Step validationEntitiesStep() {
        System.out.println(validationLoadProperties.getChunk());
        return stepBuilders
                .get(LOAD_ANNOTATION_DBXREF_ENTITIES_STEP_NAME)
                .<DBXRefEntity, DBXRefEntity>chunk(Integer.parseInt(validationLoadProperties.getChunk()))
                .reader(dbXrefReader())
                .writer(validationEntitiesAggregator())
                .listener(logStepListener())
                .listener(skipLogListener())
                .build();
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
    ValidationEntitiesAggregator validationEntitiesAggregator() {
        return new ValidationEntitiesAggregator();
    }

    @Bean
    ValidationEntityChecker validationEntityChecker(ValidationEntities<ValidationEntity> validationEntities) {
        return new ValidationEntityChecker(validationEntities);
    }

    @Bean
    ValidationEntities<ValidationEntity> validationEntities(ValidationEntitiesAggregator validationEntitiesAggregator) {
        return new ValidationEntitiesImpl(validationEntitiesAggregator);
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

    private FlatFileItemReader<DBXRefEntity> dbXrefReader() {
        FlatFileItemReader<DBXRefEntity> reader = new FlatFileItemReader<>();
        reader.setLineMapper(dbXrefEntityLineMapper());
        reader.setLinesToSkip(HEADER_LINES);
        try {
            reader.setResource(new GZIPResource(new FileSystemResource(validationLoadProperties.getValidationFile())));
        } catch (IOException e) {
            LOGGER.error(
                    "Failed to load " + validationLoadProperties.getValidationFile() + ". " +
                            "No corresponding information for this annotation validation will be available.", e);
        }

        return reader;
    }

    private class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }
}
