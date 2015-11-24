package uk.ac.ebi.quickgo.config;

import uk.ac.ebi.quickgo.model.ontology.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.converter.GODocConverter;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;
import uk.ac.ebi.quickgo.document.ontology.OntologyType;
import uk.ac.ebi.quickgo.service.ontology.impl.OntologyServiceImpl;

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
@Import({RepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.service"})
public class ServiceConfig {

    @Bean
    public OntologyService<GOTerm> goOntologyService(OntologyRepository ontologyRepository) {
        return new OntologyServiceImpl<>(ontologyRepository, goDocumentConverter(), OntologyType.GO);
    }

    @Bean
    public GODocConverter goDocumentConverter() {
        return new GODocConverter();
    }
}
