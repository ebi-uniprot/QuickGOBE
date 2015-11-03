package uk.ac.ebi.quickgo.search.ontology;

import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.search.ontology.DocumentMocker.Relation.createRelation;

/**
 * This class is used to show which queries are necessary to successfully search the index, e.g.,
 * how to search for an ECO term.
 *
 * Changes made to the ontology core's schema.xml are instantly reflected by the tests
 * defined in this class.
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


    @Test
    public void shouldFindRelationParentWithExactQuery() {
        SolrTerm relation = createRelation();
        relation.setChild("GO:0006601");
        relation.setParent("GO:0006600");
        searchEngine.indexDocument(relation);

        QueryResponse queryResponse = searchEngine.getQueryResponse("parent:GO\\:0006600");
        assertThat(queryResponse.getResults().size(), is(1));
    }

    @Test
    public void shouldFindRelationParentWithIDOnly() {
        SolrTerm relation = createRelation();
        relation.setChild("GO:0006601");
        relation.setParent("GO:0006600");
        searchEngine.indexDocument(relation);

        QueryResponse queryResponse = searchEngine.getQueryResponse("parent:0006600");
        assertThat(queryResponse.getResults().size(), is(1));
    }

    @Test
    public void shouldFindRelationChildWithExactQuery() {
        SolrTerm relation = createRelation();
        relation.setChild("GO:0006601");
        relation.setParent("GO:0006600");
        searchEngine.indexDocument(relation);

        QueryResponse queryResponse = searchEngine.getQueryResponse("child:GO\\:0006601");
        assertThat(queryResponse.getResults().size(), is(1));
    }

    @Test
    public void shouldFindRelationChildWithIDOnly() {
        SolrTerm relation = createRelation();
        relation.setChild("GO:0006601");
        relation.setParent("GO:0006600");
        searchEngine.indexDocument(relation);

        QueryResponse queryResponse = searchEngine.getQueryResponse("child:0006601");
        assertThat(queryResponse.getResults().size(), is(1));
    }
}
