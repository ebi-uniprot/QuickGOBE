package uk.ac.ebi.quickgo.annotation.download.header;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;

import com.google.common.base.Preconditions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide the details of the latest ontology sources.
 *
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 10:37
 * Created with IntelliJ IDEA.
 */
public class OntologyHeaderInfo {
    private static Logger LOGGER = LoggerFactory.getLogger(OntologyHeaderInfo.class);

    private final Path ontologyPath;
    private List<String> savedOntologyLines;
    private FileTime previousTimeStamp;

    public OntologyHeaderInfo(Path ontologyPath) {
        Preconditions.checkArgument(ontologyPath != null, "The path to the ontology file must not be null");
        this.ontologyPath = ontologyPath;
    }

    /**
     * The version details of the current ontology sources.
     * @return a list of versions, commonly GO & ECO.
     */
    List<String> versions() {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(ontologyPath);

            if (!lastModifiedTime.equals(previousTimeStamp)) {
                previousTimeStamp = lastModifiedTime;
                savedOntologyLines = GZIPFiles.lines(ontologyPath)
                        .skip(1)
                        .map(s -> s.substring(s.indexOf("http:")))
                        .collect(Collectors.toList());

            }
        } catch (Exception e) {
            savedOntologyLines = Collections.emptyList();
            LOGGER.error("Failed to load the version of the ontology", e);
        }
        return savedOntologyLines;
    }
}
