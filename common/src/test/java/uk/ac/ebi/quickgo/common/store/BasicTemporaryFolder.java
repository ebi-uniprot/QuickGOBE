package uk.ac.ebi.quickgo.common.store;

import java.io.File;
import java.io.IOException;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

/**
 * Creates a temporary storage folder which is deleted on exit. Use this class with
 * the annotation, @ClassRule in tests requiring a temporary data store.
 *
 * Note: the purpose of this class is to more robustly guarantee automatic cleanup of
 * a temporary folder. It has been noted that deleting a {@link TemporaryFolder} explicitly
 * in an @AfterClass/@After test method, does not reliably delete the folder.
 *
 * Created 02/03/17
 * @author Edd
 */
public class BasicTemporaryFolder extends ExternalResource {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    public File getRoot() {
        return temporaryFolder.getRoot();
    }

    @Override
    public void before() throws IOException {
        temporaryFolder.create();
    }

    @Override
    public void after() {
        temporaryFolder.delete();
    }
}