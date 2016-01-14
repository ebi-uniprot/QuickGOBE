package uk.ac.ebi.quickgo.repo.solr;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

/**
 * Creates a temporary solr data store, which is deleted on exit. Use this class with
 * the annotation, @ClassRule in tests requiring a temporary solr data store.
 *
 * Created 13/11/15
 * @author Edd
 */
public class TemporarySolrDataStore extends ExternalResource {
    // the property defining the solr data store, used in solr core's solrconfig.xml files
    private static final String SOLR_DATA_DIR = "solr.data.dir";

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    public void before() throws IOException {
        temporaryFolder.create();
        System.setProperty(SOLR_DATA_DIR, temporaryFolder.getRoot().getAbsolutePath());
    }

    @Override
    public void after() {
        temporaryFolder.delete();
    }
}
