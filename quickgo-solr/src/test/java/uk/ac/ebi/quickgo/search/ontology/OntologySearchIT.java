package uk.ac.ebi.quickgo.search.ontology;

import uk.ac.ebi.quickgo.search.AbstractSearchEngine;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrievalImpl;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.contains;
import static uk.ac.ebi.quickgo.search.AbstractSearchEngine.filterResultsTo;
import static uk.ac.ebi.quickgo.search.ontology.DocumentMocker.Relation.createRelation;
import static uk.ac.ebi.quickgo.search.ontology.DocumentMocker.Replaces.createReplaces;
import static uk.ac.ebi.quickgo.search.ontology.DocumentMocker.Term.createTerm;

/**
 * This class is used to show which queries are necessary to successfully search the index, e.g.,
 * how to search for a GO term.
 * <p/>
 * Changes made to the ontology core's schema.xml are instantly reflected by the tests
 * defined in this class.
 * <p/>
 * Example tests: queries sent by {@link TermRetrieval} can be tested here first, before needing
 * to move libraries/configurations re-indexing, then testing server-side.
 * <p/>
 * Please use {@link DocumentMocker} to add documents to the search engine, before trying to search for them.
 *
 * @see DocumentMocker
 *
 * Created 02/11/15
 * @author Edd
 */
public class OntologySearchIT {
    @ClassRule
    public static final OntologySearchEngine searchEngine = new OntologySearchEngine();

    /**
     * Clean index before each test
     */
    @Before
    public void cleanIndex() {
        searchEngine.removeAllDocuments();
    }

    /**
     * Testing {@link TermRetrievalImpl#findById(String)} when id matches.
     *
     * Example query: "id:X
     *          OR (docType:relation AND (child:X OR parent:X))
     *          OR (docType:replace AND obsoleteId:X)"
     */
    @Test
    public void findByIDWhenExactIDSupplied() {
        SolrTerm term = createTerm();
        term.setId("GO:0000001");
        searchEngine.indexDocument(term);

        QueryResponse queryResponse = searchEngine.getQueryResponse(TermField.ID.getValue() + ":GO\\:0000001");

        assertThat(filterResultsTo(queryResponse, TermField.ID.getValue()), contains("GO:0000001"));
    }

    /**
     * Testing {@link TermRetrievalImpl#findById(String)} when child matches.
     *
     * Example query: "id:X
     *          OR (docType:relation AND (child:X OR parent:X))
     *          OR (docType:replace AND obsoleteId:X)"
     */
    @Test
    public void findByIDWhenExactChildSupplied() {
        SolrTerm term = createRelation();
        term.setChild("GO:0000002");
        searchEngine.indexDocument(term);

        QueryResponse queryResponse = searchEngine.getQueryResponse(TermField.CHILD.getValue() + ":GO\\:0000002");

        assertThat(filterResultsTo(queryResponse, TermField.CHILD.getValue()), contains("GO:0000002"));
    }

    /**
     * Testing {@link TermRetrievalImpl#findById(String)} when parent matches.
     *
     * Example query: "id:X
     *          OR (docType:relation AND (child:X OR parent:X))
     *          OR (docType:replace AND obsoleteId:X)"
     */
    @Test
    public void findByIDWhenExactParentSupplied() {
        SolrTerm term = createRelation();
        term.setParent("GO:0000002");
        searchEngine.indexDocument(term);

        QueryResponse queryResponse = searchEngine.getQueryResponse(TermField.PARENT.getValue() + ":GO\\:0000002");

        assertThat(filterResultsTo(queryResponse, TermField.PARENT.getValue()), contains("GO:0000002"));
    }

    /**
     * Testing {@link TermRetrievalImpl#findById(String)} when obsolete matches.
     *
     * Example query: "id:X
     *          OR (docType:relation AND (child:X OR parent:X))
     *          OR (docType:replace AND obsoleteId:X)"
     */
    @Test
    public void findByIDWhenExactObsoleteIdSupplied() {
        SolrTerm term = createReplaces();
        term.setObsoleteId("GO:0000003");
        searchEngine.indexDocument(term);

        QueryResponse queryResponse = searchEngine.getQueryResponse(TermField.OBSOLETE_ID.getValue() + ":GO\\:0000003");

        assertThat(filterResultsTo(queryResponse, TermField.OBSOLETE_ID.getValue()), contains("GO:0000003"));
    }



}
