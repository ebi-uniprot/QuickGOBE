package uk.ac.ebi.quickgo.ontology.metadata;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataMarker;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataStringOnly;
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
    private static final String SERVICE = "GO";

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
            List<MetaDataMarker> metaDataMarkers = GZIPFiles.lines(ontologyPath)
                                            .skip(1)
                                            .filter(s -> s.startsWith("GO"))
                                            .map(s -> {
                                                  String[] a = s.split("\t");
                                                  MetaDataStringOnly metaDataStringOnly = new MetaDataStringOnly();
                                                  metaDataStringOnly.add(MetaData.VERSION, a[2]);
                                                  metaDataStringOnly.add(MetaData.TIMESTAMP, a[1]);
                                                  return metaDataStringOnly;
                                              })
                                            .collect(toList());

            MetaData metaData = new MetaData();
            metaDataMarkers.forEach( metaDataMarker -> metaData.add(SERVICE, metaDataMarker));
            if(metaData.getProperties().keySet().size() == 0){
                throw new ServiceConfigException("Unable to load metadata for Service "+ SERVICE);
            }
            return metaData;
        } catch (Exception e) {
            throw new ServiceConfigException(e);
        }

    }
}
