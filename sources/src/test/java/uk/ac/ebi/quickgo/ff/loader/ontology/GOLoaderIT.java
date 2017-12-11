package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.io.File;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * TODO: *HAVE* to enable creating a GOSourceFiles instance without depending on the 50+ source files currently used.
 *
 * For now, this test simply checks one can successfully load
 * a {@link GeneOntology} instance from a directory containing the
 * source files.
 *
 * Created by edd on 11/12/2015.
 */
public class GOLoaderIT {

    //private static final String SOURCE_FILE_DIR = "/home/eddturner/working/quickgo-local/quickgo-data/ff";
    private static final String SOURCE_FILE_DIR = "C:\\Users\\twardell\\Projects\\QuickGo\\data_ontology";

    /**
     * Ignored because it's not portable. Depends on hard-coded path.
     */
    @Ignore @Test
    public void canLoadGOInstance() {
        GOSourceFiles sourceFiles = new GOSourceFiles(new File(SOURCE_FILE_DIR));
        GOLoader goLoader = new GOLoader(sourceFiles);
        GeneOntology geneOntology;
        try {
            geneOntology = goLoader.load();
            assertThat(geneOntology, is(notNullValue()));

            //Highly input data specific
            assertEquals(((GOTerm) geneOntology.getTerm("GO:0003774")).getBlacklist().get(0).getMethodId(),
                    "IPR001609|IPR002928");
        } catch (Exception e) {
            assertThat(e, is(nullValue()));
        }
    }
}
