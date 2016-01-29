package uk.ac.ebi.quickgo.ontology.common.config;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.xml.sax.SAXException;

@Configuration
public class RepoConfig {
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SolrServer solrServer(@Value("${solr.host}") String solrUrl) throws IOException, SAXException,
                                                                               ParserConfigurationException {
        return new HttpSolrServer(solrUrl);
    }

    @Bean
    public SolrTemplate ontologyTemplate(SolrServer server) throws ParserConfigurationException, SAXException,
                                                                   IOException {
        return new SolrTemplate(server, "ontology");
    }

    @Bean
    public OntologyRepository ontologyRepository(SolrTemplate ontologyTemplate) {
        return new SolrRepositoryFactory(ontologyTemplate)
                .getRepository(OntologyRepository.class);
    }
}