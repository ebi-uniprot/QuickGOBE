package uk.ac.ebi.quickgo.annotation.download;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.ac.ebi.quickgo.annotation.download.header.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;

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
    private static final int DEFAULT_DOWNLOAD_EMITTER_TIMEOUT_MILLIS = 40 * 60 * 1000;
    private static final Path DEFAULT_ONTOLOGY_PATH = Paths.get("ONTOLOGY_IRI.dat.gz");

    private TaskExecutorProperties taskExecutor = new TaskExecutorProperties();
    private int defaultEmitterTimeout = DEFAULT_DOWNLOAD_EMITTER_TIMEOUT_MILLIS;
    private Resource ontologySource;

    @Bean
    public WebMvcConfigurer asyncWebMvcConfigurerAdapter() {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setDefaultTimeout(defaultEmitterTimeout);
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

    @Bean
    public HeaderCreatorFactory headerCreatorFactory(OntologyHeaderInfo ontology) throws IOException {
        Map<String, HeaderCreator> headerCreatorMap = new HashMap<>();
        headerCreatorMap.put(GAF_SUB_TYPE, new GafHeaderCreator(ontology));
        headerCreatorMap.put(GPAD_SUB_TYPE, new GpadHeaderCreator(ontology));
        headerCreatorMap.put(TSV_SUB_TYPE, new TSVHeaderCreator());
        return new HeaderCreatorFactory(headerCreatorMap);
    }

    @Bean
    OntologyHeaderInfo ontology() throws IOException {
        Path osPath = ontologySource != null ? Paths.get(ontologySource.getURI()) : DEFAULT_ONTOLOGY_PATH;
        return new OntologyHeaderInfo(osPath);
    }

    public TaskExecutorProperties getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutorProperties taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setDefaultEmitterTimeout(int defaultEmitterTimeout) {
        this.defaultEmitterTimeout = defaultEmitterTimeout;
    }

    public void setOntologySource(Resource ontologySource) {
        this.ontologySource = ontologySource;
    }
}
