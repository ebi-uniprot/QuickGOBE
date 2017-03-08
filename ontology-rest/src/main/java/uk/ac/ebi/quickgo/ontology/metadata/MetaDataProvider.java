package uk.ac.ebi.quickgo.ontology.metadata;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Populate a MetaData instance with information about the data provided by this service
 *
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 10:51
 * Created with IntelliJ IDEA.
 */
public class MetaDataProvider {
    private final Path ontologyPath;

    public MetaDataProvider(Path ontologyPath) {
        Preconditions.checkArgument(ontologyPath != null, "The path to the source of the Ontology metadata cannot " +
                "be null.");
        this.ontologyPath = ontologyPath;
    }

    /**
     * Lookup the MetaData for the Ontology service
     * @return MetaData instance
     */
    public MetaData lookupMetaData() {
        try {
            List<MetaData> goLines = GZIPFiles.lines(ontologyPath)
                                              .skip(1)
                                              .filter(s -> s.startsWith("GO"))
                                              .map(s -> {
                                                  String[] a = s.split("\t");
                                                  return new MetaData(a[2], a[1]);
                                              })
                                              .collect(toList());
            Preconditions.checkState(goLines.size() == 1, "Unable to read the correct number of lines for Ontology " +
                    "metadata");
            return goLines.get(0);
        } catch (Exception e) {
            throw new ServiceConfigException(e);
        }

    }
}
