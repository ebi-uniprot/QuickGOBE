package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.*;
import uk.ac.ebi.quickgo.annotation.validation.service.ValidationEntityChecker;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.infrastructure.item.file.transform.LineTokenizer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(ValidationProperties.class)
public class ValidationConfig {

    public static final String LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME = "loadAnnotationDbXrefEntitiesValidationValues";
    private static final String LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME = "validationLoadingJob";
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationConfig.class);
    private static final String COLUMNS_DELIMITER = "\t";

    @Autowired
    private ValidationProperties validationProperties;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean Job validationJob(Step validationEntitiesStep) {
        Preconditions.checkArgument(Objects.nonNull(validationEntitiesStep), "Cannot run %s as %s is null",
                                    LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME, validationEntitiesStep );
        return new JobBuilder(LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME, jobRepository)
                .start(validationEntitiesStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    Step validationEntitiesStep(ValidationEntitiesAggregator validationEntitiesAggregator) {
        try {
            return new StepBuilder(LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME, jobRepository)
                    .<DBXRefEntity, DBXRefEntity>chunk(validationProperties.getChunk(), transactionManager)
                    .reader(dbXrefReader())
                    .writer(validationEntitiesAggregator)
                    .listener(logStepListener())
                    .listener(skipLogListener())
                    .build();
        } catch (IOException e) {
            LOGGER.error("Exception occurred while building " + LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME, e );
        }
        return null;
    }

    @Bean
    ValidationEntitiesAggregator validationEntitiesAggregator(ValidationEntityChecker validationEntityChecker) {
        return new ValidationEntitiesAggregator(validationEntityChecker);
    }

    @Bean
    ValidationEntityChecker validationEntityChecker() {
        return new ValidationEntityChecker();
    }

    private LineMapper<DBXRefEntity> dbXrefEntityLineMapper() {
        DefaultLineMapper<DBXRefEntity> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(dbXrefLineTokenizer());
        lineMapper.setFieldSetMapper(dbXrefFieldSetMapper());
        return lineMapper;
    }

    private LineTokenizer dbXrefLineTokenizer() {
        return new DelimitedLineTokenizer(COLUMNS_DELIMITER);
    }

    private FieldSetMapper<DBXRefEntity> dbXrefFieldSetMapper() {
        return new StringToDbXrefEntityMapper();
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

    private FlatFileItemReader<DBXRefEntity> dbXrefReader() throws IOException {
        FlatFileItemReader<DBXRefEntity> reader = new FlatFileItemReader<>(dbXrefEntityLineMapper());
        reader.setLinesToSkip(validationProperties.getHeaderLines());
        reader.setResource(new GZIPResource(validationProperties.getValidationResource()));
        return reader;
    }

    private class GZIPResource extends InputStreamResource implements Resource {
        GZIPResource(Resource delegate) throws IOException {
            super(new GZIPInputStream(delegate.getInputStream()));
        }
    }
}
