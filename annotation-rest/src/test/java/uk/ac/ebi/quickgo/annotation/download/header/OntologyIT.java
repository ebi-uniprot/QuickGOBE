package uk.ac.ebi.quickgo.annotation.download.header;


import uk.ac.ebi.quickgo.annotation.download.DownloadConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 14:12
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DownloadConfig.class)
public class OntologyIT {
    private static final String ECO_VERSION = "http://purl.obolibrary.org/obo/eco/releases/2017-01-06/eco.owl";
    private static final String GO_VERSION = "http://purl.obolibrary.org/obo/go/releases/2017-01-12/go.owl";

    private static final Resource resource = new ClassPathResource("ONTOLOGY_IRI.dat.gz");
    private Path path;


    @Before
    public void setup() throws IOException {
        path = Paths.get(resource.getURI());
    }

    @Test
    public void loadOntologySuccessfully(){
        Ontology ontology = new Ontology(path);

        List<String> versions = ontology.versions();

        assertThat(versions, hasItems(GO_VERSION, ECO_VERSION));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingOntologyWithNullArgumentToConstructorCreatesException(){
        new Ontology(null);
    }

}
