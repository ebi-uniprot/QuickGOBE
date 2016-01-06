package uk.ac.ebi.quickgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring configuration for Solr repositories, responsible for scanning
 * and making accessible components defined in the specified {@link ComponentScan}
 * packages, and loading properties from the application.properties file.
 *
 * These values are subsequently used by other configuration classes, such as
 * {@link SolrServerProperties}.
 *
 * Created 12/11/15
 * @author Edd
 */
@Configuration
@ComponentScan({
        "uk.ac.ebi.quickgo.repo"
})
@PropertySource("classpath:application.properties")
public class RepoConfig {

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}