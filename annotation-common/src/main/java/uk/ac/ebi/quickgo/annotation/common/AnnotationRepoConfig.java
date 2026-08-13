package uk.ac.ebi.quickgo.annotation.common;

import java.nio.file.FileSystems;
import java.util.List;
import java.util.Optional;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.core.CoreContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import uk.ac.ebi.quickgo.common.SolrCollectionName;

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

    @Bean(destroyMethod = "shutdown")
    @Profile("embeddedServer")
    public CoreContainer coreContainer(@Value("${solr.solr.home}") String solrHome) {
        return CoreContainer.createAndLoad(FileSystems.getDefault().getPath(solrHome));
    }

    @Bean
    @Profile("embeddedServer")
    public SolrClient embeddedSolrServer(CoreContainer coreContainer) {
        return new EmbeddedSolrServer(coreContainer, SolrCollectionName.ANNOTATION);
    }

    @Bean
    public AnnotationRepository annotationRepository(SolrClient solrClient) {
        return new AnnotationRepositoryImpl(solrClient);
    }
}
