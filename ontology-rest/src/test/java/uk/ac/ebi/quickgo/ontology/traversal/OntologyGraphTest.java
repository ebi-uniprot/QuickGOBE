package uk.ac.ebi.quickgo.ontology.traversal;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    @Test(expected = IllegalArgumentException.class)
    public void findingPathsBetweenSameVertexThrowsException() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.StringEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("1"),
                OntologyRelation.CAPABLE_OF_PART_OF
        );
    }

    @Test
    public void findAllPathsBetween1LevelOfAncestorsViaAllRelations() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.StringEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("2")
        );
        System.out.println(paths);
        assertThat(paths.size(), is(2));
    }

    @Test
    public void findAllPathsBetween1LevelOfAncestorsVia1Relation() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.StringEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("2"),
                OntologyRelation.CAPABLE_OF_PART_OF
        );
        System.out.println(paths);
        assertThat(paths.size(), is(1));
    }

    @Test
    public void findAllPathsBetween2LevelsOfAncestorsViaAllRelations() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.StringEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("3")
        );
        System.out.println(paths);
        assertThat(paths.size(), is(2));
    }

    @Test
    public void findZeroPathsBetween2LevelsOfAncestorsVia1Relation() {
        ontologyGraph.addRelationships(
                asList(
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF),
                        createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF),
                        createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN)
                )
        );

        List<List<OntologyGraph.StringEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("3"),
                OntologyRelation.CAPABLE_OF_PART_OF
        );
        System.out.println(paths);
        assertThat(paths.size(), is(0));
    }

    @Test
    public void findAllPathsBetween2LevelsOfAncestorsVia2Relations() {
        OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF);
        OntologyRelationship v1_CP_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF);
        OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN);

        ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

        List<List<OntologyGraph.StringEdge>> paths = ontologyGraph.paths(
                goID("1"),
                goID("3"),
                OntologyRelation.CAPABLE_OF_PART_OF,
                OntologyRelation.OCCURS_IN
        );
        System.out.println(paths);
        assertThat(paths, contains(edgesFor(v1_CP_v2, v2_OI_v3)));
    }

    @Test
    public void findAncestorsViaAllRelations() {
        OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF);
        OntologyRelationship v1_CP_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF);
        OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN);

        ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

        Set<String> ancestors = ontologyGraph.ancestors(goID("1"));

        assertThat(ancestors, containsInAnyOrder(goID("2"), goID("3")));
    }

    @Test
    public void findAncestorsVia1Relation() {
        OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF);
        OntologyRelationship v1_CP_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF);
        OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN);

        ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

        Set<String> ancestors = ontologyGraph.ancestors(goID("1"), OntologyRelation.CAPABLE_OF_PART_OF);

        assertThat(ancestors, contains(goID("2")));
    }

    @Test
    public void findDescendantsViaAllRelations() {
        OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF);
        OntologyRelationship v1_CP_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF);
        OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN);

        ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

        Set<String> ancestors = ontologyGraph.descendants(goID("3"));

        assertThat(ancestors, containsInAnyOrder(goID("1"), goID("2")));
    }

    @Test
    public void findDescendantsVia1Relation() {
        OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF);
        OntologyRelationship v1_CP_v2 = createRelationship(goID("1"), goID("2"), OntologyRelation.CAPABLE_OF_PART_OF);
        OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelation.OCCURS_IN);

        ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

        Set<String> ancestors = ontologyGraph.descendants(goID("3"), OntologyRelation.OCCURS_IN);

        assertThat(ancestors, contains(goID("2")));
    }

    private static List<OntologyGraph.LabelledEdge> edgesFor(OntologyRelationship... relations) {
        List<OntologyGraph.LabelledEdge> edges = new ArrayList<>();
        for (OntologyRelationship relation : relations) {
            OntologyGraph.StringEdge labelledEdge = new OntologyGraph.StringEdge(
                    relation.child, relation.parent, OntologyRelation.getByShortName(relation.relationship)
            );
            edges.add(labelledEdge);
        }
        return edges;
    }

}