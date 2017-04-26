package uk.ac.ebi.quickgo.ontology.metadata;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataConfigProperties;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataStringOnly;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Configure the provision of metadata for the Ontology service.
 *
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 10:55
 * Created with IntelliJ IDEA.
 */
@Configuration
public class MetaDataConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataConfig.class);
    private static final Path DEFAULT_METADATA_PATH = Paths.get("ONTOLOGY_IRI.dat.gz");
    private static final String SERVICE = "go";
    private static final Function<Path, MetaData> MAPPER = (Path p) -> {
        try (Stream<String> stream = GZIPFiles.lines(p)) {
            MetaDataStringOnly metaDataStringOnly = new MetaDataStringOnly();
            stream.forEach(s -> {
                String[] a = s.split("\t");
                metaDataStringOnly.add(MetaData.VERSION, a[2]);
                metaDataStringOnly.add(MetaData.TIMESTAMP, a[1]);
            });
            MetaData metaData = new MetaData();
            metaData.add(SERVICE, metaDataStringOnly);
            return metaData;
        }
    };

    @Bean
    @ConfigurationProperties(prefix = "ontology.metadata")
    public MetaDataConfigProperties metaDataConfigProperties() {
        return new MetaDataConfigProperties();
    }

    @Bean
    MetaDataProvider metaDataProvider(MetaDataConfigProperties metaDataConfigProperties) throws IOException {
        Path metaDataPath = DEFAULT_METADATA_PATH;
        final Resource source = metaDataConfigProperties.getSource();
        if (source != null) {
            try {
                metaDataPath = Paths.get(source.getURI());
            } catch (IOException e) {
                LOGGER.error("Failed to get the URI of the metadata source " + source, e);
            }
        }
        return new MetaDataProvider(MAPPER, metaDataPath);
    }
}
