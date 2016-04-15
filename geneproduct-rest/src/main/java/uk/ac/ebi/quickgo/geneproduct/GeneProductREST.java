package uk.ac.ebi.quickgo.geneproduct;

 /**
 * Runnable class to start an embedded server to host the defined RESTful components.
 *
 * @author Tony Wardell
 * Date: 04/04/2016
 * Time: 11:39
 * Created with IntelliJ IDEA.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import uk.ac.ebi.quickgo.geneproduct.service.ServiceConfig;

@SpringBootApplication
@ComponentScan({"uk.ac.ebi.quickgo.geneproduct.controller", "uk.ac.ebi.quickgo.rest"})
@Import({ServiceConfig.class})
public class GeneProductREST {

	/**
	 * Ensures that placeholders are replaced with property values
	 */
	@Bean
	static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static void main(String[] args) {
		SpringApplication.run(GeneProductREST.class, args);
	}
}
