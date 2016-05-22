package uk.ac.ebi.quickgo.ontology.traversal;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created 20/05/16
 *
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
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.CONSIDER)
                )
        );
        assertThat(ontologyGraph.getVertices().size(), is(3));
        assertThat(ontologyGraph.getEdges().size(), is(3));
    }

    @Test
    public void findAllPathsBetweenLevel1AncestorsViaAllRelations() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.LabelledEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("3")
        );
        System.out.println(paths);
        assertThat(paths.size(), is(2));
    }

    @Test
    public void findZeroPathsBetweenLevel1AncestorsVia1Relation() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.LabelledEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("3"),
                OntologyRelation.CAPABLE_OF_PART_OF
        );
        System.out.println(paths);
        assertThat(paths.size(), is(0));
    }

    @Test
    public void findAllPathsBetweenLevel1AncestorsVia2Relations() {
        OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF);
        OntologyRelationship v1_CP_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF);
        OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN);

        ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

        List<List<OntologyGraph.LabelledEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("3"),
                OntologyRelation.CAPABLE_OF_PART_OF,
                OntologyRelation.OCCURS_IN
        );
        System.out.println(paths);
        assertThat(paths.size(), is(1));
        assertThat(paths, contains(edgesFor(v1_CP_v2, v2_OI_v3)));
    }

    private static List<OntologyGraph.LabelledEdge> edgesFor(OntologyRelationship... relations) {
        List<OntologyGraph.LabelledEdge> edges = new ArrayList<>();
        for (OntologyRelationship relation : relations) {
            OntologyGraph.LabelledEdge<String> labelledEdge = new OntologyGraph.LabelledEdge<>(
                    relation.child, relation.parent, OntologyRelation.getByShortName(relation.relationship)
            );
            edges.add(labelledEdge);
        }
        return edges;
    }

}