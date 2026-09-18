package uk.ac.ebi.quickgo.annotation.common;

import java.util.List;
import java.util.Optional;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Publishes the configuration beans of the annotation repository.
 *
 * Created 14/04/16
 * @author Edd
 */
@Configuration
public class AnnotationRepoConfig {

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
    public AnnotationRepository annotationRepository(SolrClient solrClient) {
        return new AnnotationRepositoryImpl(solrClient);
    }

    private SolrClient notRequiredSolrClientForTests(){
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
