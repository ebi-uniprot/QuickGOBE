package uk.ac.ebi.quickgo.ontology.metadata;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 10:55
 * Created with IntelliJ IDEA.
 */
@Configuration
@ConfigurationProperties(prefix = "metadata.ontology")
public class MetaDataConfig {
    private static final Path DEFAULT_ONTOLOGY_PATH = Paths.get("ONTOLOGY_IRI.dat.gz");
    @Value("${metadata.ontology.source}")
    private Resource source;

    @Bean
    MetaDataProvider metaDataProvider() throws IOException {
        Path osPath = source != null ? Paths.get(source.getURI()) : DEFAULT_ONTOLOGY_PATH;
        return new MetaDataProvider(osPath);
    }
}
