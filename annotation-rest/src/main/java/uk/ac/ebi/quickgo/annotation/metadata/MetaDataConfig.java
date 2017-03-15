package uk.ac.ebi.quickgo.annotation.metadata;

import uk.ac.ebi.quickgo.rest.metadata.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import static java.util.stream.Collectors.toList;

/**
 * Configure the provision of metadata for the Annotation service.
 *
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 10:55
 * Created with IntelliJ IDEA.
 */
@Configuration
public class MetaDataConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataConfig.class);
    private static final String SERVICE = "annotation";
    private static final Path DEFAULT_METADATA_PATH = Paths.get("goa_uniprot.gpa-version");
    private static final Function<Path, MetaData> MAPPER = (Path p) -> {
        try (Stream<String> stream = Files.lines(p)){
            MetaDataStringOnly metaDataStringOnly = new MetaDataStringOnly();
            stream.forEach(s -> metaDataStringOnly.add(MetaData.TIMESTAMP, extractTimestamp(s)));
            MetaData metaData  = new MetaData();
            metaData.add(SERVICE, metaDataStringOnly);
            return metaData;
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    };

    private static String extractTimestamp(String s) {
        return s.substring(s.indexOf('2'));
    }

    @Bean
    @ConfigurationProperties(prefix = "annotation.metadata")
    public MetaDataConfigProperties metaDataConfigProperties() {
        return new MetaDataConfigProperties();
    }

    @Bean
    MetaDataProvider metaDataProvider(MetaDataConfigProperties metaDataConfigProperties) {
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
