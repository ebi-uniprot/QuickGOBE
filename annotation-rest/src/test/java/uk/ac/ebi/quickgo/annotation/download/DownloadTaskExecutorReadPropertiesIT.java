package uk.ac.ebi.quickgo.annotation.download;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Check properties for configuring the download {@link ThreadPoolTaskExecutor}, were read and used.
 *
 * Created 23/01/17
 * @author Edd
 */
@ActiveProfiles("download-task-executor-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DownloadTaskExecutorReadPropertiesIT.FakeApplication.class)
public class DownloadTaskExecutorReadPropertiesIT {
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void corePoolSizeIsPopulated() {
        assertThat(taskExecutor.getCorePoolSize(), is(10));
    }

    @Test
    public void maxPoolSizeIsPopulated() {
        assertThat(taskExecutor.getMaxPoolSize(), is(20));
    }

    @Test
    public void queueCapacityIsPopulated() {
        assertThat(taskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity(), is(100));
    }

    @Test
    public void keepAliveIsPopulated() {
        assertThat(taskExecutor.getKeepAliveSeconds(), is(600));
    }

    @Test
    public void allowCoreThreadTimeoutIsPopulated() {
        assertThat(taskExecutor.getThreadPoolExecutor().allowsCoreThreadTimeOut(), is(false));
    }

    /**
     * ThreadPoolTaskExecutor does not provide a getter to enable checking of its value,
     * therefore testing verifies the method was called.
     */
    @Test
    public void verifySetWaitForTasksToCompleteOnShutdownIsCalled() {
        TaskExecutorProperties properties = new TaskExecutorProperties();
        boolean expectedWaitForTasksToComplete = true;
        properties.setWaitForTasksToCompleteOnShutdown(expectedWaitForTasksToComplete);
        ThreadPoolTaskExecutor taskExecutor = mock(ThreadPoolTaskExecutor.class);

        DownloadConfig config = new DownloadConfig();
        config.setTaskExecutor(properties);
        config.taskExecutor(taskExecutor);

        verify(taskExecutor, times(1)).setWaitForTasksToCompleteOnShutdown(expectedWaitForTasksToComplete);
    }

    @Profile("download-task-executor-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(DownloadConfig.class)
    public static class FakeApplication {}
}