package uk.ac.ebi.quickgo.annotation.validation.service;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig.LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME;


/**
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 10:51
 * Created with IntelliJ IDEA.
 */

@SpringBootTest(classes = {JobTestRunnerConfig.class, ValidationConfig.class})
class ValidationEntityCheckerIT {

    @Autowired
    private ValidationEntityChecker validator;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void validationSucceedsIfKnownDb(){
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(LOAD_ANNOTATION_DBX_REF_ENTITIES_STEP_NAME);
        assertThat(jobExecution.getStatus(), Is.is(BatchStatus.COMPLETED));
        assertThat(validator.isValid("PMID:123456"), is(true));
        assertThat(validator.isValid("IntAct:EBI-10043081"), is(true));
        assertThat(validator.isValid("ComplexPortal:CPX-101"), is(true));
    }
}
