package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created 20/05/16
 * @author Edd
 */
public class OntologyGraphTest {
    private OntologyGraph ontologyGraph;

    @Before
    public void setUp() {
        ontologyGraph = new OntologyGraph();
    }

    private OntologyRelationship createRelationship(String child, String parent, OntologyRelation relation) {
        OntologyRelationship relationship = new OntologyRelationship();
        relationship.child = child;
        relationship.parent = parent;
        relationship.relationship = relation.getShortName();
        return relationship;
    }

    private String goID(String value) {
        return "GO:" + value;
    }

    private String ecoID(String value) {
        return "ECO:" + value;
    }

    private String validID(String value) {
        return goID(value);
    }

    @Test
    public void initialisedGraphContainsNothing() {
        assertThat(ontologyGraph.getVertices().size(), is(0));
        assertThat(ontologyGraph.getEdges().size(), is(0));
    }

    @Test
    public void addingRelationshipsSucceeds() {
        ontologyGraph.addRelationships(
                Arrays.asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.CONSIDER)
                )
        );
        assertThat(ontologyGraph.getVertices().size(), is(3));
        assertThat(ontologyGraph.getEdges().size(), is(3));
    }

}