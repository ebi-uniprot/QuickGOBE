package uk.ac.ebi.quickgo.ontology.common;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
import org.xml.sax.SAXException;

/**
 * Publishes the configuration beans of the ontology repository.
 */
@Configuration
public class RepoConfig {
    Logger LOGGER  =  org.slf4j.LoggerFactory.getLogger(RepoConfig.class);

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "solrServer")
    @Profile("httpServer")
    public SolrServer httpSolrServer(@Value("${solr.host}") String solrUrl)  {


        return new HttpSolrServer(solrUrl);
    }

    @Bean(name = "solrServer")
    @Profile("embeddedServer")
    public SolrServer embeddedSolrServer(SolrServerFactory solrServerFactory) {

        LOGGER.info("Using embedded server");
        return solrServerFactory.getSolrServer();
    }

    @Bean
    @Profile("embeddedServer")
    public SolrServerFactory solrServerFactory(CoreContainer coreContainer)
            throws IOException, SAXException, ParserConfigurationException {
        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, null);
        return new MulticoreSolrServerFactory(embeddedSolrServer);
    }

    @Bean
    @Profile("embeddedServer")
    public CoreContainer coreContainer(@Value("${solr.solr.home}") String solrHome) {
        CoreContainer container = new CoreContainer(new File(solrHome).getAbsolutePath());
        container.load();
        return container;
    }

    @Bean
    public SolrTemplate ontologyTemplate(SolrServer solrServer)  {
        return new SolrTemplate(solrServer, "ontology");
    }

    @Bean
    public OntologyRepository ontologyRepository(SolrTemplate ontologyTemplate) {
        LOGGER.info("Returning ontology repo {}", ontologyTemplate.toString());

        return new SolrRepositoryFactory(ontologyTemplate)
                .getRepository(OntologyRepository.class);
    }
}
