package uk.ac.ebi.quickgo.repo.main;

import uk.ac.ebi.quickgo.repo.TemporarySolrDataStore;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * Currently used for debugging and seeing what's going on during indexing.
 *
 * TODO: add meaningful tests
 *
 * Created 18/12/15
 * @author Edd
 */
public class QuickGOIndexOntologyMainIT {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Test
    public void testIndexingRuns() {
        QuickGOIndexOntologyMain.main(new String[]{});
    }
}