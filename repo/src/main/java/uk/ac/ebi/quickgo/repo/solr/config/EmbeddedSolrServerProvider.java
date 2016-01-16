package uk.ac.ebi.quickgo.repo.solr.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import uk.ac.ebi.quickgo.repo.solr.io.geneproduct.GeneProductRepository;
import uk.ac.ebi.quickgo.repo.solr.io.ontology.OntologyRepository;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Context to create an {@link EmbeddedSolrServer}, which is useful for testing purposes.
 * <p>
 * Important: we do not use the @EnableSolrRepositories annotations with multicore facilities
 * here, because Spring's handling of this, in combination with the EmbeddedSolrServer,
 * does not work well. Therefore, define the multicore Repository beans as needed here
 * and use them as normal via the @Autowire annotation in classes that require them.
 * Also, one can specify SolrTemplates for each Repository to give the ability to
 * access directly the underlying SolrServer of the Repository.
 *
 * Created 11/11/15
 *
 * @author Edd
 */
@Component
@Profile("dev")
public class EmbeddedSolrServerProvider {

    private final SolrServerProperties solrProperties;

    @Autowired
    public EmbeddedSolrServerProvider(SolrServerProperties solrProperties) {
        this.solrProperties = solrProperties;
    }

    @Bean
    public SolrServer solrServer(SolrServerFactory solrServerFactory) {
        return solrServerFactory.getSolrServer();
    }

    @Bean
    public SolrServerFactory solrServerFactory(CoreContainer coreContainer) throws IOException, SAXException, ParserConfigurationException {
        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, null);
        return new MulticoreSolrServerFactory(embeddedSolrServer);
    }

    @Bean
    public OntologyRepository ontologyRepository(SolrTemplate ontologyTemplate) {
        return new SolrRepositoryFactory(ontologyTemplate)
                .getRepository(OntologyRepository.class);
    }

    @Bean
    public GeneProductRepository geneProductRepository(SolrTemplate geneProductTemplate) {
        return new SolrRepositoryFactory(geneProductTemplate)
                .getRepository(GeneProductRepository.class);
    }

    @Bean
    public SolrTemplate ontologyTemplate(SolrServerFactory solrServerFactory) {
        return new SolrTemplate(solrServerFactory.getSolrServer("ontology"));
    }

    @Bean
    public SolrTemplate geneProductTemplate(SolrServerFactory solrServerFactory) {
        return new SolrTemplate(solrServerFactory.getSolrServer("geneproduct"));
    }

    @Bean
    public CoreContainer coreContainer() {
        CoreContainer container = new CoreContainer(new File(solrProperties.getSolrHome()).getAbsolutePath());
        container.load();
        return container;
    }
}