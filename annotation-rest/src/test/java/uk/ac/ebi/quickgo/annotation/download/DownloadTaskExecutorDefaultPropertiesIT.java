package uk.ac.ebi.quickgo.annotation.download;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
@SpringBootTest(classes = DownloadTaskExecutorDefaultPropertiesIT.FakeApplication.class)
class DownloadTaskExecutorDefaultPropertiesIT {
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    void corePoolSizeIsPopulated() {
        assertThat(taskExecutor.getCorePoolSize(), is(DEFAULT_CORE_POOL_SIZE));
    }

    @Test
    void maxPoolSizeIsPopulated() {
        assertThat(taskExecutor.getMaxPoolSize(), is(MAX_POOL_SIZE));
    }

    @Test
    void queueCapacityIsPopulated() {
        assertThat(taskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity(), is(QUEUE_CAPACITY));
    }

    @Test
    void keepAliveIsPopulated() {
        assertThat(taskExecutor.getKeepAliveSeconds(), is(KEEP_ALIVE_SECONDS));
    }

    @Test
    void allowCoreThreadTimeoutIsPopulated() {
        assertThat(taskExecutor.getThreadPoolExecutor().allowsCoreThreadTimeOut(), is(ALLOW_CORE_THREAD_TIMEOUT));
    }

    /**
     * ThreadPoolTaskExecutor does not provide a getter to enable checking of its value,
     * therefore testing verifies the method was called.
     */
    @Test
    void verifySetWaitForTasksToCompleteOnShutdownIsCalled() {
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