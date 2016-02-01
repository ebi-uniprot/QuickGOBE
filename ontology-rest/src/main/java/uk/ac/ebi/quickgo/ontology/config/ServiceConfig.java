package uk.ac.ebi.quickgo.ontology.config;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.config.RepoConfig;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.OntologyServiceImpl;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the service layer, which depends on the repositories
 * made available by {@link RepoConfig}. Services to additionally make accessible
 * are defined in specified the {@link ComponentScan} packages.
 *
 * Created 19/11/15
 * @author Edd
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.ontology.service"})
@Import(RepoConfig.class)
public class ServiceConfig {
    @Bean
    public OntologyService<GOTerm> goOntologyService(OntologyRepository ontologyRepository) {
        return new OntologyServiceImpl<>(ontologyRepository, goDocumentConverter(), OntologyType.GO);
    }

    private GODocConverter goDocumentConverter() {
        return new GODocConverter();
    }

    @Bean
    public OntologyService<ECOTerm> ecoOntologyService(OntologyRepository ontologyRepository) {
        return new OntologyServiceImpl<>(ontologyRepository, ecoDocConverter(), OntologyType.ECO);
    }

    private ECODocConverter ecoDocConverter() {
        return new ECODocConverter();
    }

    // search beans
//    @Bean
//    public SearchService<OBOTerm> ontologySearchService(RequestRetrieval<OBOTerm> ontologySolrRequestRetrieval) {
//        return new OntologySearchServiceImpl(ontologySolrRequestRetrieval);
//    }
//
//    @Bean
//    public RequestRetrieval<OBOTerm> ontologySolrRequestRetrieval(
//            SolrTemplate ontologyTemplate,
//            QueryRequestConverter<SolrQuery> solrSelectQueryRequestConverter,
//            ServiceProperties serviceProperties) {
//
//        OntologySolrQueryResultConverter resultConverter = new OntologySolrQueryResultConverter(
//                new DocumentObjectBinder(),
//                new GODocConverter(),
//                new ECODocConverter()
//        );
//
//        return new SolrRequestRetrieval<>(
//                ontologyTemplate.getSolrServer(),
//                solrSelectQueryRequestConverter,
//                resultConverter,
//                serviceProperties.getOntologySearchSolrReturnedFields());
//    }
}
