package uk.ac.ebi.quickgo.geneproduct.service;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.RepoConfig;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverterImpl;
import uk.ac.ebi.quickgo.geneproduct.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.ControllerHelper;
import uk.ac.ebi.quickgo.rest.search.ControllerHelperImpl;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.service.ServiceHelper;
import uk.ac.ebi.quickgo.rest.service.ServiceHelperImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the service layer, which depends on the repositories
 * made available by {@link RepoConfig} and {@link SearchServiceConfig}. Services
 * to additionally make accessible are defined in specified the {@link ComponentScan} packages.
 *
 * Created 19/11/15
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.geneproduct.service"})
@Import({RepoConfig.class, SearchServiceConfig.class})
public class ServiceConfig {
    @Bean
    public GeneProductService goOntologyService(GeneProductRepository geneProductRepository) {
        return new GeneProductServiceImpl(
                serviceHelper(),
                geneProductRepository,
                geneProductDocumentConverter());
    }

    private GeneProductDocConverter geneProductDocumentConverter() {
        return new GeneProductDocConverterImpl();
    }

    private ServiceHelper serviceHelper() {
        return new ServiceHelperImpl(queryStringSanitizer());
    }

    private QueryStringSanitizer queryStringSanitizer() {
        return new SolrQueryStringSanitizer();
    }

    @Bean
    public ControllerHelper controllerHelper() {
        return new ControllerHelperImpl();
    }
}
