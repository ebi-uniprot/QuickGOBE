package uk.ac.ebi.quickgo.common.store;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.*;

public class SolrContainerTestSetup {

    // Static Singleton Container: Shared across ALL test classes, We do NOT use @Container annotation here because we manage lifecycle manually for reuse
    protected static final SolarTestContainer solrContainer;

    static {
        solrContainer = new SolarTestContainer();
        solrContainer.start();
    }

    // Dynamic Property Injection: Feeds test contaienr Solr into Spring Environment
    @DynamicPropertySource
    static void registerSolrProperties(DynamicPropertyRegistry registry) {
        String solrHost = "http://localhost:"+ solrContainer.getSolrPort() + "/solr";
        registry.add("integration.test.solr.host.full.url", () -> solrHost);
    }
}