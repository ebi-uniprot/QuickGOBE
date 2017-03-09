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

    private static final  Function<Path,List<MetaData>> FAKE_MAPPER = (Path p)
                                                            -> Arrays.asList(new MetaData("1.0",""));
    private static final String SERVICE_NAME = "Test Service";
    private static final Path PATH_TO_NOWHERE = Paths.get("/path/to/nowhere");

    @Test(expected = IllegalArgumentException.class)
    public void serviceIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(null, FAKE_MAPPER, PATH_TO_NOWHERE, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapperIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(SERVICE_NAME, null, PATH_TO_NOWHERE, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void rawStreamIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(SERVICE_NAME, FAKE_MAPPER, null, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedNumberOfMetaDataLinesThrowsIllegalArgumentException(){
        new MetaDataProvider(SERVICE_NAME, FAKE_MAPPER, PATH_TO_NOWHERE, 0 );
    }

    @Test
    public void readFileForMetaDataWithoutProblems() throws Exception{
        MetaDataProvider metaDataProvider = new MetaDataProvider(SERVICE_NAME, FAKE_MAPPER, PATH_TO_NOWHERE, 1 );
        List<MetaData> metaDataList = metaDataProvider.lookupMetaData();
        assertThat(metaDataList, hasSize(1));
        assertThat(metaDataList.get(0).timestamp, is(""));
        assertThat(metaDataList.get(0).version, is("1.0"));
    }

    @Test(expected = ServiceConfigException.class)
    public void readFileForMetaDataProducesUnexpectedNumberOfLines() throws Exception{
        MetaDataProvider metaDataProvider = new MetaDataProvider(SERVICE_NAME, FAKE_MAPPER, PATH_TO_NOWHERE, 2 );
        metaDataProvider.lookupMetaData();
    }
}
