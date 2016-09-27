package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ff.files.ontology.ECOSourceFiles;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.ECOLoader;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.graphics.service.GraphImageServiceImpl;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraphTraversal;
import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyGraphConfig;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;

import java.io.File;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static java.util.Objects.requireNonNull;

/**
 * Spring configuration for the service layer, which depends on the repositories
 * made available by {@link OntologyRepoConfig} and {@link SearchServiceConfig}. Services
 * to additionally make accessible are defined in specified the {@link ComponentScan} packages.
 *
 * Created 19/11/15
 * @author Edd
 */
@Configuration
@ComponentScan({"uk.ac.ebi.quickgo.ontology.service, uk.ac.ebi.quickgo.graphics.service"})
@Import({OntologyRepoConfig.class, OntologyGraphConfig.class})
public class ServiceConfig {
    @Value("${graphics.ontology.source}")
    private String sourceFile;

    @Bean
    public OntologyService<GOTerm> goOntologyService(OntologyRepository ontologyRepository,
            OntologyGraphTraversal ontologyGraphTraversal) {
        return new OntologyServiceImpl<>(
                ontologyRepository,
                goDocumentConverter(),
                OntologyType.GO,
                queryStringSanitizer(),
                ontologyGraphTraversal);
    }

    @Bean
    public OntologyService<ECOTerm> ecoOntologyService(OntologyRepository ontologyRepository,
            OntologyGraphTraversal ontologyGraphTraversal) {
        return new OntologyServiceImpl<>(
                ontologyRepository,
                ecoDocConverter(),
                OntologyType.ECO,
                queryStringSanitizer(),
                ontologyGraphTraversal);
    }

    @Bean
    public GraphImageService graphImageService() throws IllegalStateException {
        File sourceFileDir = new File(sourceFile);
        Optional<GeneOntology> geneOntologyOptional =
                new GOLoader(new GOSourceFiles(requireNonNull(sourceFileDir))).load();
        Optional<EvidenceCodeOntology> evidenceCodeOntologyOptional =
                new ECOLoader(new ECOSourceFiles(requireNonNull(sourceFileDir))).load();

        GraphImageService graphImageService = null;

        if (geneOntologyOptional.isPresent() && evidenceCodeOntologyOptional.isPresent()) {
            graphImageService = new GraphImageServiceImpl(
                    geneOntologyOptional.get(),
                    evidenceCodeOntologyOptional.get());
        }
        return graphImageService;
    }

    private GODocConverter goDocumentConverter() {
        return new GODocConverter();
    }

    private ECODocConverter ecoDocConverter() {
        return new ECODocConverter();
    }

    private QueryStringSanitizer queryStringSanitizer() {
        return new SolrQueryStringSanitizer();
    }
}
