package uk.ac.ebi.quickgo.ontology.metadata;

import uk.ac.ebi.quickgo.rest.service.ServiceConfigException;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test the attempted reading of the file that contains metadata for the ontology service.
 * Successful reading of the file is tested in the endpoint integration test.
 *
 * @author Tony Wardell
 * Date: 08/03/2017
 * Time: 10:03
 * Created with IntelliJ IDEA.
 */
public class MetaDataProviderTest {

    @Test(expected = IllegalArgumentException.class)
    public void fileForMetaDataIsNull(){
        new MetaDataProvider(null);
    }

    @Test(expected = ServiceConfigException.class)
    public void fileForMetaDataDoesNotExist(){
        MetaDataProvider metaDataProvider = new MetaDataProvider(Paths.get("/does/not/exist"));
        metaDataProvider.lookupMetaData();
    }

    @Test(expected = ServiceConfigException.class)
    public void fileExistsButDoesNotContainExpectedContent() throws IOException {
        Resource resource = new ClassPathResource("ONTOLOGY_IRI_BROKEN.dat.gz");
        MetaDataProvider metaDataProvider = new MetaDataProvider( Paths.get(resource.getURI()));
        metaDataProvider.lookupMetaData();
    }

}
