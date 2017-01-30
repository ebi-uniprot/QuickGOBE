package uk.ac.ebi.quickgo.annotation.service.download;

import org.springframework.core.task.TaskExecutor;

/**
 * Records properties that can be used to configure a {@link TaskExecutor}.
 *
 * Created 23/01/17
 * @author Edd
 */
public class TaskExecutorProperties {
    static final int DEFAULT_CORE_POOL_SIZE = 3;
    static final int MAX_POOL_SIZE = 5;
    static final int QUEUE_CAPACITY = 100;
    static final int KEEP_ALIVE_SECONDS = 20 * 60;
    static final boolean ALLOW_CORE_THREAD_TIMEOUT = false;
    static final boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = true;

    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maxPoolSize = MAX_POOL_SIZE;
    private int queueCapacity = QUEUE_CAPACITY;
    private int keepAliveSeconds = KEEP_ALIVE_SECONDS;
    private boolean allowCoreThreadTimeout = ALLOW_CORE_THREAD_TIMEOUT;
    private boolean waitForTasksToCompleteOnShutdown = WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN;

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
        this.allowCoreThreadTimeout = allowCoreThreadTimeout;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public boolean isAllowCoreThreadTimeout() {
        return allowCoreThreadTimeout;
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }
}
