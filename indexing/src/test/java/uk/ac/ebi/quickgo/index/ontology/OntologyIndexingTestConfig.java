package uk.ac.ebi.quickgo.index.ontology;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created 12/01/16
 * @author Edd
 */
@Profile("QuickGOIndexOntologyMainIT")
@Configuration
public class OntologyIndexingTestConfig {
//    private static final Logger LOGGER = getLogger(OntologyIndexingTestConfig.class);

//    @ClassRule
//    public static final BasicTemporaryFolder SITE_MAP_TEMP_FOLDER = new BasicTemporaryFolder();

//    @Bean
//    @Primary
//    public OntologyReader ontologyReader() {
//        return mock(OntologyReader.class);
//    }

//    @Bean
//    @Primary
//    public WebSitemapGenerator sitemapGenerator(BasicTemporaryFolder siteMapTempFolder) {
//        try {
//            return WebSitemapGenerator
//                    .builder(DEFAULT_QUICKGO_FRONTEND_URL, siteMapTempFolder.getRoot())
//                    .build();
//        } catch (MalformedURLException e) {
//            LOGGER.error("Sitemap URL is malformed", e);
//            throw new IllegalStateException(e);
//        }
//    }
//
//    @Bean
//    public BasicTemporaryFolder siteMapFolder() {
////        return new BasicTemporaryFolder();
//        return SITE_MAP_TEMP_FOLDER;
//    }
}
