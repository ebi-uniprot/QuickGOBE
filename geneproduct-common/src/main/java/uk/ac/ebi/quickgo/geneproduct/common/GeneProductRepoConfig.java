package uk.ac.ebi.quickgo.geneproduct.common;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

/**
 * Publishes the configuration beans of the Gene Product repository.
 */
@Configuration
public class GeneProductRepoConfig {

    @Bean
    @Profile("httpServer")
    public SolrClient httpSolrServer(@Value("${zookeeper.hosts}") List<String> zkHosts) {
        return new CloudSolrClient.Builder(zkHosts, Optional.empty()).build();
    }

    @Bean
    @Profile("embeddedServer")
    public SolrClient embeddedSolrServer(@Value("${integration.test.solr.host.full.url:-}") String solrHostUrl) {
        if(solrHostUrl == null || solrHostUrl.isBlank()) {
            return notRequiredSolrClientForTests();
        }
        return new HttpJdkSolrClient.Builder(solrHostUrl).build();
    }

    @Bean
    public GeneProductRepository geneProductRepository(SolrClient solrClient) {
        return new GeneProductRepositoryImpl(solrClient);
    }

    private SolrClient notRequiredSolrClientForTests() {
        return new SolrClient() {
            @Override
            public NamedList<Object> request(SolrRequest<?> solrRequest, String s) {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }
}