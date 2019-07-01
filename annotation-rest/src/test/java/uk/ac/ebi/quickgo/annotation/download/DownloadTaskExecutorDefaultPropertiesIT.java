package uk.ac.ebi.quickgo.annotation.download;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.annotation.download.TaskExecutorProperties.*;

/**
 * Check default properties for configuring the download {@link ThreadPoolTaskExecutor}, were used.
 *
 * Created 23/01/17
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DownloadTaskExecutorDefaultPropertiesIT.FakeApplication.class)
public class DownloadTaskExecutorDefaultPropertiesIT {
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void corePoolSizeIsPopulated() {
        assertThat(taskExecutor.getCorePoolSize(), is(DEFAULT_CORE_POOL_SIZE));
    }

    @Test
    public void maxPoolSizeIsPopulated() {
        assertThat(taskExecutor.getMaxPoolSize(), is(MAX_POOL_SIZE));
    }

    @Test
    public void queueCapacityIsPopulated() {
        assertThat(taskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity(), is(QUEUE_CAPACITY));
    }

    @Test
    public void keepAliveIsPopulated() {
        assertThat(taskExecutor.getKeepAliveSeconds(), is(KEEP_ALIVE_SECONDS));
    }

    @Test
    public void allowCoreThreadTimeoutIsPopulated() {
        assertThat(taskExecutor.getThreadPoolExecutor().allowsCoreThreadTimeOut(), is(ALLOW_CORE_THREAD_TIMEOUT));
    }

    /**
     * ThreadPoolTaskExecutor does not provide a getter to enable checking of its value,
     * therefore testing verifies the method was called.
     */
    @Test
    public void verifySetWaitForTasksToCompleteOnShutdownIsCalled() {
        ThreadPoolTaskExecutor taskExecutor = mock(ThreadPoolTaskExecutor.class);

        DownloadConfig config = new DownloadConfig();
        config.taskExecutor(taskExecutor);

        verify(taskExecutor, times(1)).setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(DownloadConfig.class)
    public static class FakeApplication {}
}