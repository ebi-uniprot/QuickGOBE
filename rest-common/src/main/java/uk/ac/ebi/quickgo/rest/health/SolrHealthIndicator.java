package uk.ac.ebi.quickgo.rest.health;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class SolrHealthIndicator implements HealthIndicator {

    static final String COLLECTION_PROPERTY = "health.solr.collection";

    private final ObjectProvider<SolrClient> solrClients;
    private final String collection;

    public SolrHealthIndicator(
            ObjectProvider<SolrClient> solrClients,
            @Value("${" + COLLECTION_PROPERTY + ":}") String collection) {
        this.solrClients = solrClients;
        this.collection = collection;
    }

    @Override
    public Health health() {
        SolrClient solrClient = solrClients.getIfAvailable();
        if (solrClient == null) {
            return Health.unknown().build();
        }
        try {
            if (isCollectionConfigured()) {
                solrClient.ping(collection);
            } else {
                solrClient.ping();
            }
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    private boolean isCollectionConfigured() {
        return collection != null && !collection.trim().isEmpty();
    }
}
