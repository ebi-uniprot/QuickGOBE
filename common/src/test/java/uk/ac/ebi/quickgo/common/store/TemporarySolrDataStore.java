package uk.ac.ebi.quickgo.common.store;

import java.io.File;
import java.io.IOException;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

/**
 * Creates a temporary solr data store, which is deleted on exit. Use this class with
 * the annotation, @ClassRule in tests requiring a temporary solr data store.
 *
 * Created 13/11/15
 * @author Edd
 */
public class TemporarySolrDataStore extends ExternalResource {
    // the following properties are defined in the solrconfig.xml files
    private static final String SOLR_DATA_DIR = "solr.data.dir";
    private static final String SOLR_PLUGIN_JAR = "solr.similarity.plugin";

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    public void before() throws IOException {
        temporaryFolder.create();
        System.setProperty(SOLR_DATA_DIR, temporaryFolder.getRoot().getAbsolutePath());
        System.setProperty(SOLR_PLUGIN_JAR, pathToSimilarityPlugin());
    }

    private String pathToSimilarityPlugin() {
        File file = new File("../solr-plugin/target/similarity_plugin.jar");

        if(!file.exists()) {
            throw new IllegalStateException("similarity_plugin.jar does not exist. Please generated the jar before " +
                    "running the tests");
        }

        return file.getAbsolutePath();
    }

    @Override
    public void after() {
        temporaryFolder.delete();
    }
}
