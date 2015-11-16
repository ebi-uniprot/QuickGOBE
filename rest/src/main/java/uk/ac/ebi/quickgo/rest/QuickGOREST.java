package uk.ac.ebi.quickgo.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created 16/11/15
 * @author Edd
 */
@SpringBootApplication
@ComponentScan({
        "uk.ac.ebi.quickgo"
})
public class QuickGOREST {

    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        SpringApplication.run(QuickGOREST.class, args);
    }
}
