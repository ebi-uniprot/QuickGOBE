package uk.ac.ebi.quickgo.rest.metadata;

import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Populate a MetaData instance with information about the data provided by this service
 *
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 10:51
 * Created with IntelliJ IDEA.
 */
public class MetaDataProvider {
    private final String service;
    private final Function<Path, List<MetaData>> metaDataMapper;
    private final Path path;
    private final int expectedNumberOfLines;

    public MetaDataProvider(String service, Function<Path, List<MetaData>> metaDataMapper, Path path, int noOfLines) {
        checkArgument(Objects.nonNull(service), "The name of the service for the meta data provider " +
                "cannot be null.");
        checkArgument(Objects.nonNull(metaDataMapper), "The metadata mapper cannot be null.");
        checkArgument(Objects.nonNull(path), "The path cannot be null.");
        checkArgument(noOfLines > 0, "The expected number of lines of metadata for the " +
                "service should not be greater zero.");
        this.service = service;
        this.metaDataMapper = metaDataMapper;
        this.path = path;
        this.expectedNumberOfLines = noOfLines;
    }

    /**
     * Lookup the MetaData for the selected service
     * @return MetaData instance
     */
    public List<MetaData> lookupMetaData() {
        try {
            List<MetaData> metaLines = metaDataMapper.apply(path);
            checkState(metaLines.size() == expectedNumberOfLines, "Unable to read the correct number of lines for " +
                    "%s metadata", service);
            return metaLines;
        } catch (Exception e) {
            throw new ServiceConfigException(e);
        }
    }
}
