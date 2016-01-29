package uk.ac.ebi.quickgo.ontology.common.config;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
import org.xml.sax.SAXException;

@Configuration
@PropertySource("classpath:application.properties")
public class RepoTestConfig {
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SolrServer solrServer(SolrServerFactory solrServerFactory) {
        return solrServerFactory.getSolrServer();
    }

    @Bean
    public SolrServerFactory solrServerFactory(CoreContainer coreContainer)
            throws IOException, SAXException, ParserConfigurationException {
        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, null);
        return new MulticoreSolrServerFactory(embeddedSolrServer);
    }

    @Bean
    public OntologyRepository ontologyRepository(SolrTemplate ontologyTemplate) {
        return new SolrRepositoryFactory(ontologyTemplate)
                .getRepository(OntologyRepository.class);
    }

    @Bean
    public SolrTemplate ontologyTemplate(SolrServerFactory solrServerFactory) {
        return new SolrTemplate(solrServerFactory.getSolrServer("ontology"));
    }

    @Bean
    public CoreContainer coreContainer(@Value("${solr.solr.home}") String solrHome) {
        CoreContainer container = new CoreContainer(new File(solrHome).getAbsolutePath());
        container.load();
        return container;
    }
}
