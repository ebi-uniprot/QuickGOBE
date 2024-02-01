package uk.ac.ebi.quickgo.client.service.loader.presets;

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
class JobTestRunnerConfig {
    @Bean
    public JobLauncherTestUtils utils() {
        return new JobLauncherTestUtils();
    }
}
