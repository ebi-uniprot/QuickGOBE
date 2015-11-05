package uk.ac.ebi.quickgo.search.miscellaneous;

import uk.ac.ebi.quickgo.search.ontology.DocumentMocker;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static uk.ac.ebi.quickgo.search.AbstractSearchEngine.filterResultsTo;
import static uk.ac.ebi.quickgo.search.miscellaneous.DocumentMocker.XRefDB.createXRefDB;

/**
 * This class is used to show which queries are necessary to successfully search the index, e.g.,
 * how to search for a cross-reference abbreviation.
 * <p/>
 * Changes made to the ontology core's schema.xml are instantly reflected by the tests
 * defined in this class.
 * <p/>
 * Example tests: queries sent by {@link MiscellaneousUtil} can be tested here first, before needing
 * to move libraries/configurations re-indexing, then testing server-side.
 * <p/>
 * Please use {@link DocumentMocker} to add documents to the search engine, before trying
 * to search for them.
 *
 * @see DocumentMocker
 *
 * Created 02/11/15
 * @author Edd
 */
public class MiscellaneousSearchIT {
    @ClassRule
    public static final MiscellaneousSearchEngine searchEngine = new MiscellaneousSearchEngine();

    /**
     * Clean index before each test
     */
    @Before
    public void cleanIndex() {
        searchEngine.removeAllDocuments();
    }

    /**
     * Exact xref abbrev. user search.
     *
     * See {@link MiscellaneousUtilImpl#getDBInformation}
     */
    @Test
    public void findXRefDBWithExactQuery() {
        SolrMiscellaneous stats = createXRefDB();
        stats.setXrefAbbreviation("bioPIXIE_MEFIT");

        searchEngine.indexDocument(stats);

        QueryResponse queryResponse = searchEngine.getQueryResponse(
                MiscellaneousField.TYPE.getValue() + ":" + stats.getDocType() +
                        " AND " +
                        MiscellaneousField.XREFABBREVIATION.getValue() + ":bioPIXIE_MEFIT");

        assertThat(filterResultsTo(queryResponse, MiscellaneousField.XREFABBREVIATION.getValue()),
                contains("bioPIXIE_MEFIT"));
    }

    /**
     * Partial xref abbrev. user search.
     *
     * See {@link uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtilImpl#getDBInformation}
     *
     * If this behaviour is not wanted, please remove.
     */
    @Test
    public void findXRefDBWithPartialQuery() {
        SolrMiscellaneous stats = createXRefDB();
        stats.setXrefAbbreviation("bioPIXIE_MEFIT");

        searchEngine.indexDocument(stats);

        QueryResponse queryResponse = searchEngine.getQueryResponse(
                MiscellaneousField.TYPE.getValue() + ":" + stats.getDocType() +
                        " AND " +
                        MiscellaneousField.XREFABBREVIATION.getValue() + ":bioPIXIE");

        assertThat(filterResultsTo(queryResponse, MiscellaneousField.XREFABBREVIATION.getValue()),
                contains("bioPIXIE_MEFIT"));
    }
}
