package uk.ac.ebi.quickgo.annotation.converter;

import java.io.IOException;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;

/**
 * @author Tony Wardell
 * Date: 26/01/2017
 * Time: 10:47
 * Created with IntelliJ IDEA.
 */
@Configuration
class HeaderTestConfig {

    @Value("${annotation.download.ontologySource}")
    private Resource resource;

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public AnnotationDownloadFileHeader annotationDownloadFileHeader() throws IOException {
        return new AnnotationDownloadFileHeader(Paths.get(resource.getURI()));
    }
}
