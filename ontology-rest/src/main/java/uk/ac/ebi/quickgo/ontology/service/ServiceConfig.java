package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.RepoConfig;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;

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
@Import({RepoConfig.class, SearchServiceConfig.class})
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
}
