package uk.ac.ebi.quickgo.annotation.validation.service;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Utility class providing beans for testing batch jobs.
 *
 * Created 12/01/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
public class JobTestRunnerConfig {
    @Bean
    public JobLauncherTestUtils utils() {
        return new JobLauncherTestUtils();
    }
}
