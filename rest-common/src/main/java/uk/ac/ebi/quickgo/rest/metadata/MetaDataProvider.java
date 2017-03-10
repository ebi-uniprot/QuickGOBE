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
    private final Function<Path, MetaData> metaDataMapper;
    private final Path path;

    public MetaDataProvider(Function<Path, MetaData> metaDataMapper, Path path) {
        checkArgument(Objects.nonNull(metaDataMapper), "The metadata mapper cannot be null.");
        checkArgument(Objects.nonNull(path), "The path cannot be null.");
        this.metaDataMapper = metaDataMapper;
        this.path = path;
    }

    /**
     * Lookup the MetaData for the selected service
     * @return MetaData instance
     */
    public MetaData lookupMetaData() {
        try {
            return metaDataMapper.apply(path);
        } catch (Exception e) {
            throw new ServiceConfigException(e);
        }
    }
}
