package uk.ac.ebi.quickgo.repo.solr.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Context to create an {@link HttpSolrServer}, suitable for production purposes.
 *
 * Created 11/11/15
 * @author Edd
 */
@Component
@Profile("prod")
public class SolrServerProvider {
    private final SolrServerProperties solrProperties;

    @Autowired
    public SolrServerProvider(SolrServerProperties solrProperties) {
        this.solrProperties = solrProperties;
    }

    @Bean
    public SolrServer solrServer() throws IOException, SAXException, ParserConfigurationException {
        return new HttpSolrServer(this.solrProperties.getSolrHost());
    }

}
