package uk.ac.ebi.quickgo.annotation;

import uk.ac.ebi.quickgo.annotation.coterms.CoTermConfig;
import uk.ac.ebi.quickgo.annotation.download.DownloadConfig;
import uk.ac.ebi.quickgo.annotation.metadata.MetaDataConfig;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.annotation.service.statistics.StatisticsServiceConfig;
import uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig;
import uk.ac.ebi.quickgo.rest.controller.SwaggerConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * The RESTful service configuration for Annotations
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
@SpringBootApplication(exclude = {SolrRepositoriesAutoConfiguration.class})
@ComponentScan({"uk.ac.ebi.quickgo.annotation.controller",
        "uk.ac.ebi.quickgo.rest",
        "uk.ac.ebi.quickgo.annotation.service.statistics"})
@Import({SearchServiceConfig.class, ValidationConfig.class, SwaggerConfig.class, DownloadConfig.class,
        MetaDataConfig.class, CoTermConfig.class, StatisticsServiceConfig.class})
public class AnnotationREST {

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        SpringApplication.run(AnnotationREST.class, args);
    }
}
