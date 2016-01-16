package uk.ac.ebi.quickgo.repo.solr.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.server.SolrServerFactory;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Context to create an {@link EmbeddedSolrServer}, which is useful for testing purposes.
 * <p>
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

//    @Bean
//    public SolrServer solrServer(SolrTemplate ontologyTemplate) throws IOException, SAXException, ParserConfigurationException {
////        EmbeddedSolrServerFactory factory = new EmbeddedSolrServerFactory(this.solrProperties.getSolrHome());
//        System.out.println("---------------- about to return solrServer ---------------------");
//        return ontologyTemplate.getSolrServer();
//    }

    @Bean
    public SolrServer solrServer(SolrServerFactory solrServerFactory) {
        return solrServerFactory.getSolrServer();
    }

    @Bean
    public SolrServerFactory solrServerFactory(CoreContainer coreContainer) throws IOException, SAXException, ParserConfigurationException {
//        return new EmbeddedSolrServerFactory(this.solrProperties.getSolrHome());
        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, null);
        return new MulticoreSolrServerFactory(embeddedSolrServer);
    }

    //    @Bean
//    public SolrTemplate ontologyTemplate(CoreContainer coreContainer) {
//        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, "ontology");
//        SolrTemplate solrTemplate = new SolrTemplate(embeddedSolrServer);
//        System.out.println("---------------- ontologyTemplate created ---------------------");
//        return solrTemplate;
//    }
    @Bean
    public SolrTemplate ontologyTemplate(SolrServerFactory solrServerFactory) {
        return new SolrTemplate(solrServerFactory.getSolrServer("ontology"));
    }

//    @Bean
//    public SolrTemplate geneProductTemplate(CoreContainer coreContainer) {
//        EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, "geneproduct");
//
//        SolrTemplate solrTemplate = new SolrTemplate(embeddedSolrServer);
//        System.out.println("---------------- geneProductTemplate created ---------------------");
//        return solrTemplate;
//    }

    @Bean
    public SolrTemplate geneProductTemplate(SolrServerFactory solrServerFactory) {
        return new SolrTemplate(solrServerFactory.getSolrServer("geneproduct"));
    }

    @Bean
    public CoreContainer coreContainer() {
        String home = "/Users/edd/working/git/unp.goa.quickgo/repo/src/main/cores";
        System.setProperty("solr.solr.home", home);
        CoreContainer container = new CoreContainer(home);
        container.load();
        return container;
    }
}