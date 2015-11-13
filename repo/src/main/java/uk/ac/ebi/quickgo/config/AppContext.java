package uk.ac.ebi.quickgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * A Solr application configuration class, responsible for loading
 * properties from the standard, application.properties file.
 *
 * These values are subsequently used by other configuration classes, such as
 * {@link SolrServerProperties}.
 *
 * Created 12/11/15
 * @author Edd
 */
@Configuration
@ComponentScan({
        "uk.ac.ebi.quickgo"
})
@PropertySource("classpath:application.properties")
public class AppContext {

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}