package uk.ac.ebi.quickgo.geneproduct.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepoConfig;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverterImpl;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.service.ServiceHelper;
import uk.ac.ebi.quickgo.rest.service.ServiceHelperImpl;

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

    private static final String DEFAULT_ID_VALIDATION_DB = "UniProtKB";
    private static final String DEFAULT_VALIDATION_TYPE_NAME = "protein";

    @Value("${geneproduct.db.xref.valid.regexes}")
    private String xrefValidationRegexFile;

    @Bean
    public GeneProductService goGeneProductService(GeneProductRepository geneProductRepository) {
        return new GeneProductServiceImpl(
                serviceHelper(),
                geneProductRepository,
                geneProductDocConverter());
    }

	private ServiceHelper serviceHelper(){
		return new ServiceHelperImpl(queryStringSanitizer());
	}

	private GeneProductDocConverter geneProductDocConverter() {
		return new GeneProductDocConverterImpl();
	}

	private QueryStringSanitizer queryStringSanitizer() {
		return new SolrQueryStringSanitizer();
	}

    @Bean
    public ControllerValidationHelper geneProductValidator(){
        return new ControllerValidationHelperImpl(ControllerValidationHelperImpl.MAX_PAGE_RESULTS, idValidator());
    }

    private Predicate<String> idValidator() {
        GeneProductDbXRefIDFormats
                dbXrefEntities = GeneProductDbXRefIDFormats.createWithData(geneProductLoader().load(),
                DEFAULT_ID_VALIDATION_DB,
                DEFAULT_VALIDATION_TYPE_NAME);
        return dbXrefEntities::isValidId;	}

    private DbXRefLoader geneProductLoader() {
        return new DbXRefLoader(this.xrefValidationRegexFile);
    }


}
