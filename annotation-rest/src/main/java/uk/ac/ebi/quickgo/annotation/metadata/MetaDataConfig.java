package uk.ac.ebi.quickgo.annotation.metadata;

import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String SERVICE = "Annotation";
    private static final Path DEFAULT_METADATA_PATH = Paths.get("goa_uniprot.gpa-version");
    private static final Function<Stream<String>, List<MetaData>> META_DATA_MAPPER = (Stream<String> str) -> str
        .map(s -> new MetaData(null, s.substring(s.indexOf("2"))))
        .collect(toList());
    private static final int NUMBER_OF_METADATA_LINES = 1;

    @Value("${annotation.metadata.source}")
    private Resource source;

    @Bean
    MetaDataProvider metaDataProvider() throws IOException {
        Path metaDataPath = source != null ? Paths.get(source.getURI()) : DEFAULT_METADATA_PATH;
        return new MetaDataProvider(SERVICE, META_DATA_MAPPER, Files.lines(metaDataPath), NUMBER_OF_METADATA_LINES);
    }
}
