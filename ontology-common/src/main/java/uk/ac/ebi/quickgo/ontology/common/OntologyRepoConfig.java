package uk.ac.ebi.quickgo.ontology.common;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.springframework.data.solr.server.support.HttpSolrClientFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Publishes the configuration beans of the ontology repository.
 */
@Configuration
public class OntologyRepoConfig {

    Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OntologyRepoConfig.class);

    @Bean
    public SolrClient solrClient(SolrClientFactory solrClientFactory) {
        return solrClientFactory.getSolrClient();
    }

    @Bean
    @Profile("httpServer")
    public SolrClientFactory httpSolrServerFactory(@Value("${zookeeper.hosts}") List<String> zkHosts) {
        return new HttpSolrClientFactory(new CloudSolrClient.Builder(zkHosts, Optional.empty()).build());
    }

    @Bean
    @Profile("embeddedServer")
    public SolrClientFactory embeddedSolrServerFactory(@Value("${solr.solr.home}") String solrHome)
            throws IOException, SAXException, ParserConfigurationException {
        return new EmbeddedSolrServerFactory(solrHome);
    }

    @Bean
    public SolrTemplate ontologyTemplate(SolrClientFactory solrClientFactory) {
        SolrTemplate template = new SolrTemplate(solrClientFactory);
        return template;
    }

    @Bean
    public OntologyRepository ontologyRepository(
            @Qualifier("ontologyTemplate") SolrTemplate ontologyTemplate) {
        LOGGER.info("Returning ontology repo {}", ontologyTemplate.toString());

        return new SolrRepositoryFactory(ontologyTemplate)
                .getRepository(OntologyRepository.class);
    }
}
