package uk.ac.ebi.quickgo.rest;

import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;
import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * @author Tony Wardell
 * Date: 08/03/2017
 * Time: 16:02
 * Created with IntelliJ IDEA.
 */
public class MetaDataProviderTest {

    private static final MetaData METADATA = new MetaData();
    private static final  Function<Path,MetaData> FAKE_MAPPER = (Path p) -> METADATA;
    private static final Path PATH_TO_NOWHERE = Paths.get("/path/to/nowhere");

    @Test(expected = IllegalArgumentException.class)
    public void mapperIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(null, PATH_TO_NOWHERE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(FAKE_MAPPER, null);
    }

    @Test
    public void readFileForMetaDataWithoutProblems() throws Exception{
        MetaDataProvider metaDataProvider = new MetaDataProvider(FAKE_MAPPER, PATH_TO_NOWHERE);
        MetaData md = metaDataProvider.lookupMetaData();
        assertThat(md, is(METADATA));
    }
}
