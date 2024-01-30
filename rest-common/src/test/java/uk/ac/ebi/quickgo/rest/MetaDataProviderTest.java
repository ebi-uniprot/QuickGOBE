package uk.ac.ebi.quickgo.rest;

import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 08/03/2017
 * Time: 16:02
 * Created with IntelliJ IDEA.
 */
class MetaDataProviderTest {

    private static final MetaData METADATA = new MetaData();
    private static final  Function<Path,MetaData> FAKE_MAPPER = (Path p) -> METADATA;
    private static final Path PATH_TO_NOWHERE = Paths.get("/path/to/nowhere");

    @Test
    void mapperIsNullThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new MetaDataProvider(null, PATH_TO_NOWHERE));
    }

    @Test
    void pathIsNullThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new MetaDataProvider(FAKE_MAPPER, null));
    }

    @Test
    void readFileForMetaDataWithoutProblems() {
        MetaDataProvider metaDataProvider = new MetaDataProvider(FAKE_MAPPER, PATH_TO_NOWHERE);
        MetaData md = metaDataProvider.lookupMetaData();
        assertThat(md, is(METADATA));
    }
}
