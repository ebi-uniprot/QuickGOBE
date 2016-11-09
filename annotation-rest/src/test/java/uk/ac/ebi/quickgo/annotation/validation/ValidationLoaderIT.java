package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.annotation.validation.ValidationConfig
        .LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME;

/**
 * @author Tony Wardell
 * Date: 08/11/2016
 * Time: 10:41
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ValidationConfig.class, JobTestRunnerConfig.class}, loader = SpringApplicationContextLoader.class)
public class ValidationLoaderIT {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ListItemWriter<DBXRefEntity> entityItemWriter;

    @Test
    public void successfulValidationLoad() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getJobInstance().getJobName(),
                is(LOAD_ANNOTATION_FILTERING_VALIDATION_VALUES_JOB_NAME));

        assertThat(entityItemWriter.getWrittenItems().size(), is(119));
    }
}
