package uk.ac.ebi.quickgo.index.ontology;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import java.io.File;
import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configuration beans required to generate a sitemap that references all ontology terms. The purpose
 * of the sitemap is to ensure web-crawlers are able to index each term, and therefore improve SEO for QuickGO.
 *
 * Created 04/04/17
 * @author Edd
 */
@Configuration
public class OntologySiteMapConfig {
    private static final Logger LOGGER = getLogger(OntologySiteMapConfig.class);
    
    private static final String DEFAULT_QUICKGO_FRONTEND_URL = "http://www.ebi.ac.uk/QuickGO";
    private static final String DEFAULT_QUICKGO_FRONTEND_TERM_URL = DEFAULT_QUICKGO_FRONTEND_URL + "/term";

    @Value("${frontend.urlPrefix:" + DEFAULT_QUICKGO_FRONTEND_URL + "}")
    private String frontEndUrl;

    @Value("${frontend.termUrlPrefix:" + DEFAULT_QUICKGO_FRONTEND_TERM_URL + "}")
    private String frontEndTermUrlPrefix;

    @Value("${frontend.sitemapDir:}")
    private String sitemapDir;

    @Bean
    public WebSitemapGenerator sitemapGenerator() {
        try {
            File baseDir = new File(sitemapDir);
            if (!baseDir.exists() || !baseDir.canWrite()) {
                String writableBaseDir = System.getProperty("user.home");
                LOGGER.warn("Specified sitemap base directory '{}' does not exist, or is not writable. " +
                                "Using '{}' instead.",
                        sitemapDir,
                        writableBaseDir);
                baseDir = new File(writableBaseDir);
            }

            return WebSitemapGenerator
                    .builder(frontEndUrl, baseDir)
                    .build();
        } catch (MalformedURLException e) {
            LOGGER.error("Sitemap URL is malformed", e);
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public SiteMapItemWriter siteMapOntologyWriter(WebSitemapGenerator sitemapGenerator) {
        return new SiteMapItemWriter(sitemapGenerator, frontEndTermUrlPrefix);
    }

    @Bean
    public SiteMapStepListener siteMapStepListener(WebSitemapGenerator sitemapGenerator) {
        return new SiteMapStepListener(sitemapGenerator);
    }

}
