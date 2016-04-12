package uk.ac.ebi.quickgo.geneproduct.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import uk.ac.ebi.quickgo.geneproduct.common.RepoConfig;

/**
 * @author Tony Wardell
 * Date: 04/04/2016
 * Time: 11:45
 * Created with IntelliJ IDEA.
 */
@Configuration
@Import({RepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.service.search"})
@PropertySource("classpath:search.properties")
public class SearchServiceConfig {
}
