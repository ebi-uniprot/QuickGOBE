package uk.ac.ebi.quickgo.repo.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Context to create an {@link EmbeddedSolrServer}, which is useful for testing purposes.
 *
 * Created 11/11/15
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
    public SolrServer solrServer() throws IOException, SAXException, ParserConfigurationException {
        EmbeddedSolrServerFactory factory = new EmbeddedSolrServerFactory(this.solrProperties.getSolrHome());
        return factory.getSolrServer();
    }

}