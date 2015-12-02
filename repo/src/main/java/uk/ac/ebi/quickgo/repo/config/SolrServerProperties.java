package uk.ac.ebi.quickgo.repo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A component to store Solr related properties. This provides a single instance,
 * which can easily be injected to depending classes. This approach removes the need
 * to remember original property names; just use the given accessor methods.
 *
 * Created 12/11/15
 * @author Edd
 */
@Component
public class SolrServerProperties {

    private final String solrHome;
    private final String solrHost;
    private final String solrDataDir;

    @Autowired
    public SolrServerProperties(
            @Value("${solr.host}") String solrHost,
            @Value("${solr.solr.home}") String solrHome,
            @Value("${solr.data.dir}") String solrDataDir) {
        this.solrHost = solrHost;
        this.solrHome = solrHome;
        this.solrDataDir = solrDataDir;
        setSolrSystemProperties();
    }

    /**
     * The Solr configuration files can depend on system properties, e.g., solrconfig.xml uses
     * the property, solr.data.dir. These need setting before the application can start up.
     */
    private void setSolrSystemProperties() {
        System.setProperty("solr.data.dir", this.solrDataDir); // must be a better way with spring?!
    }

    public String getSolrHome() {
        return solrHome;
    }

    public String getSolrHost() {
        return solrHost;
    }

    public String getSolrDataDir() {
        return solrDataDir;
    }
}
