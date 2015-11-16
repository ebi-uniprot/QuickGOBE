package uk.ac.ebi.quickgo.repo.config;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * Context to create an {@link EmbeddedSolrServer}, which is useful for testing purposes.
 *
 * Note: {@link EnableSolrRepositories}'s {@code basePackages} values define the
 * packages in which to look for Spring Data Repositories.
 *
 * Created 11/11/15
 * @author Edd
 */
@Component
@EnableSolrRepositories(basePackages = {"uk.ac.ebi.quickgo.repo"}, multicoreSupport = true)
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