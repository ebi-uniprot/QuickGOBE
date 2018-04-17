package uk.ac.ebi.quickgo.geneproduct.service;

import uk.ac.ebi.quickgo.common.loader.DbXRefLoader;
import uk.ac.ebi.quickgo.common.validator.DbXRefEntity;
import uk.ac.ebi.quickgo.common.validator.DbXRefEntityValidation;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepoConfig;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverterImpl;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.service.ServiceHelper;
import uk.ac.ebi.quickgo.rest.service.ServiceHelperImpl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_RESULTS;

/**
 *
 * Spring configuration for the service layer, which depends on the repositories
 * made available by {@link GeneProductRepoConfig}. Services
 * to additionally make accessible are defined in specified the {@link ComponentScan} packages.
 *
 * @author Tony Wardell
 * Date: 04/04/2016
 * Time: 11:42
 * Created with IntelliJ IDEA.
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.geneproduct.service"})
@Import({GeneProductRepoConfig.class})
public class ServiceConfig {
    Logger LOGGER = LoggerFactory.getLogger(ServiceConfig.class);

    private static final boolean DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE = true;
    @Value("${geneproduct.db.xref.valid.casesensitive:"+DEFAULT_XREF_VALIDATION_IS_CASE_SENSITIVE+"}")
    boolean xrefValidationCaseSensitive;
    @Value("${geneproduct.db.xref.valid.regexes}")
    private String xrefValidationRegexFile;

    @Bean
    public GeneProductService goGeneProductService(GeneProductRepository geneProductRepository) {
        return new GeneProductServiceImpl(
                serviceHelper(),
                geneProductRepository,
                geneProductDocConverter());
    }

    @Bean
    public ControllerValidationHelper geneProductValidator() {
        return new ControllerValidationHelperImpl(MAX_PAGE_RESULTS, idValidator());
    }

    private ServiceHelper serviceHelper() {
        return new ServiceHelperImpl(queryStringSanitizer());
    }

    private GeneProductDocConverter geneProductDocConverter() {
        return new GeneProductDocConverterImpl();
    }

    private QueryStringSanitizer queryStringSanitizer() {
        return new SolrQueryStringSanitizer();
    }

    private DbXRefEntityValidation idValidator() {
        final List<DbXRefEntity> validationList = geneProductLoader().load();
        LOGGER.info("Here is the contents of the file used for gene product validation");
        validationList.stream().forEach(v -> LOGGER.info(v.toString()));
        return DbXRefEntityValidation.createWithData(validationList);
    }

    private DbXRefLoader geneProductLoader() {
        return new DbXRefLoader(this.xrefValidationRegexFile, xrefValidationCaseSensitive);
    }

}
