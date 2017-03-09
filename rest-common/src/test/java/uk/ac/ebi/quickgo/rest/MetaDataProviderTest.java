package uk.ac.ebi.quickgo.rest;

import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * @author Tony Wardell
 * Date: 08/03/2017
 * Time: 16:02
 * Created with IntelliJ IDEA.
 */
public class MetaDataProviderTest {


    private static final  Function<Stream<String>,List<MetaData>> FAKE_MAPPER = (Stream<String> str)
                                                            -> Arrays.asList(new MetaData("1.0",""));
    private static final Stream<String>  FAKE_RAW_STREAM = Arrays.asList("2017-03-01 18:00").stream();
    private static final String SERVICE_NAME = "Test Service";

    @Test(expected = IllegalArgumentException.class)
    public void serviceIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(null, FAKE_MAPPER, FAKE_RAW_STREAM, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapperIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(SERVICE_NAME, null, FAKE_RAW_STREAM, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void rawStreamIsNullThrowsIllegalArgumentException(){
        new MetaDataProvider(SERVICE_NAME, FAKE_MAPPER, null, 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedNumberOfMetaDataLinesThrowsIllegalArgumentException(){
        new MetaDataProvider(SERVICE_NAME, FAKE_MAPPER, FAKE_RAW_STREAM, 0 );
    }
}
