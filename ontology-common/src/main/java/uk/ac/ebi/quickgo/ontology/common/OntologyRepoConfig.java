package uk.ac.ebi.quickgo.ontology.common;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.core.CoreContainer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.data.solr.server.support.MulticoreSolrClientFactory;
import org.xml.sax.SAXException;

/**
 * Publishes the configuration beans of the ontology repository.
 */
@Configuration
public class OntologyRepoConfig {
    private static final String SOLR_CORE = "ontology";

    Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OntologyRepoConfig.class);

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SolrClient solrServer(SolrClientFactory solrClientFactory) {
        return solrClientFactory.getSolrClient();
    }

    @Bean
    @Profile("httpServer")
    public SolrClientFactory httpSolrServerFactory(@Value("${solr.host}") String solrUrl) {
        return new MulticoreSolrClientFactory(new HttpSolrClient(solrUrl), SOLR_CORE);
    }

    @Bean
    @Profile("embeddedServer")
    public SolrClientFactory embeddedSolrServerFactory(CoreContainer coreContainer)
            throws IOException, SAXException, ParserConfigurationException {
        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, SOLR_CORE);
        return new MulticoreSolrClientFactory(embeddedSolrServer);
    }

    @Bean
    @Profile("embeddedServer")
    public CoreContainer coreContainer(@Value("${solr.solr.home}") String solrHome) {
        CoreContainer container = new CoreContainer(new File(solrHome).getAbsolutePath());
        container.load();
        return container;
    }

    @Bean
    public SolrTemplate ontologyTemplate(SolrClientFactory solrClientFactory) {
        SolrTemplate template = new SolrTemplate(solrClientFactory);
        template.setSolrCore(SOLR_CORE);

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
