package uk.ac.ebi.quickgo.annotation.service.download;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration beans and details used for annotation downloads.
 *
 * Created 04/01/17
 * @author Edd
 */
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "annotation.download")
public class DownloadConfig {
    private static final int DEFAULT_DOWNLOAD_EMITTER_TIMEOUT_MILLIS = 5 * 60 * 1000;

    private TaskExecutorProperties taskExecutor = new TaskExecutorProperties();
    private int defaultEmitterTimeout = DEFAULT_DOWNLOAD_EMITTER_TIMEOUT_MILLIS;

    @Bean
    public WebMvcConfigurerAdapter asyncWebMvcConfigurerAdapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setDefaultTimeout(defaultEmitterTimeout);
                super.configureAsyncSupport(configurer);
            }
        };
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor(ThreadPoolTaskExecutor configurableTaskExecutor) {
        configurableTaskExecutor.setCorePoolSize(taskExecutor.getCorePoolSize());
        configurableTaskExecutor.setMaxPoolSize(taskExecutor.getMaxPoolSize());
        configurableTaskExecutor.setQueueCapacity(taskExecutor.getQueueCapacity());
        configurableTaskExecutor.setKeepAliveSeconds(taskExecutor.getKeepAliveSeconds());
        configurableTaskExecutor.setAllowCoreThreadTimeOut(taskExecutor.isAllowCoreThreadTimeout());
        configurableTaskExecutor.setWaitForTasksToCompleteOnShutdown(taskExecutor.isWaitForTasksToCompleteOnShutdown());
        return configurableTaskExecutor;
    }

    @Bean
    public ThreadPoolTaskExecutor configurableTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    public void setTaskExecutor(TaskExecutorProperties taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public TaskExecutorProperties getTaskExecutor() {
        return taskExecutor;
    }
}