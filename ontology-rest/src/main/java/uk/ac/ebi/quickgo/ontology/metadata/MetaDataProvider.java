package uk.ac.ebi.quickgo.ontology.metadata;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;

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
        this.ontologyPath = ontologyPath;
    }

    public MetaData lookupMetaData() {
        List<MetaData> goLines = GZIPFiles.lines(ontologyPath)
                                          .skip(1)
                                          .filter(s -> s.startsWith("GO"))
                                          .limit(1)
                                          .map(s -> {
                                              String[] a = s.split("\t");
                                              return new MetaData(a[2], a[1]);
                                          })
                                          .collect(toList());
        return (goLines.size() == 1) ? goLines.get(0) : null;
    }
}
