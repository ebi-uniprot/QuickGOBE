package uk.ac.ebi.quickgo.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = {QuickGOREST.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthEndpointIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HealthEndpoint healthEndpoint;

    @LocalManagementPort
    private int managementPort;

    @Test
    void aggregateHealthIsUp() {
        org.springframework.boot.actuate.health.HealthComponent health = healthEndpoint.health();
        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void healthEndpointOnManagementPortReturnsUp() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + managementPort + "/actuator/health",
                String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().contains("\"status\":\"UP\""));
    }
}
