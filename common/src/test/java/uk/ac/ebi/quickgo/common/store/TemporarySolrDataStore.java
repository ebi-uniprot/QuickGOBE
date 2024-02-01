package uk.ac.ebi.quickgo.common.store;

import java.io.File;

import org.junit.jupiter.api.extension.*;

/**
 * Creates a temporary solr data store, which is deleted on exit.
 * Created 13/11/15
 * @author Edd
 */
public class TemporarySolrDataStore implements BeforeAllCallback {
    /* In below git commits
    7b9c277c0e8a47aa728699d02ea3e720204bed0b
    8e9443f943faad15f0c477a3570b7f94a559cf62
    Developers sided to provide solr.data.dir from scripts
    They removed this property usage from solrconfig.xml file
    Now solr index data will be generated in solr home which in
    case of test will be solr.solr.home=../solr-cores/src/main/cores
    I have put data folders in git ignore No need to created tmp dir for solr data*/
    // the following properties are defined in the solrconfig.xml files
    private static final String SOLR_DATA_DIR = "solr.data.dir";
    private static final String SOLR_PLUGIN_JAR = "solr.similarity.plugin";

    private String pathToSimilarityPlugin() {
        File file = new File("../solr-plugin/target/similarity_plugin.jar");

        if(!file.exists()) {
            throw new IllegalStateException("similarity_plugin.jar does not exist. Please generated the jar before " +
                    "running the tests");
        }

        return file.getAbsolutePath();
    }

    public void beforeAll(ExtensionContext extensionContext) {
        System.setProperty(SOLR_PLUGIN_JAR, pathToSimilarityPlugin());
    }
}
