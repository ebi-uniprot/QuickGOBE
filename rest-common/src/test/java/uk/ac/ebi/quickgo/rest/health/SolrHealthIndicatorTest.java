package uk.ac.ebi.quickgo.rest.health;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolrHealthIndicatorTest {

    @Mock
    private SolrClient solrClient;

    @Mock
    private ObjectProvider<SolrClient> solrClients;

    private SolrHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        when(solrClients.getIfAvailable()).thenReturn(solrClient);
        healthIndicator = new SolrHealthIndicator(solrClients, "annotation");
    }

    @Test
    void healthIsUpWhenSolrPingSucceeds() throws Exception {
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        verify(solrClient).ping("annotation");
    }

    @Test
    void healthIsDownWhenSolrPingFails() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("connection refused"))
                .when(solrClient).ping("annotation");

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void healthIsUnknownWhenNoSolrClientIsAvailable() {
        when(solrClients.getIfAvailable()).thenReturn(null);
        SolrHealthIndicator indicator = new SolrHealthIndicator(solrClients, "annotation");

        Health health = indicator.health();

        assertEquals(Status.UNKNOWN, health.getStatus());
    }
}
