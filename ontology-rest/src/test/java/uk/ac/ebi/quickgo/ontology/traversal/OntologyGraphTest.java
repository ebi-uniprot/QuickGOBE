package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType.*;

/**
 * Created 20/05/16
 *
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class OntologyGraphTest {
    private OntologyGraph ontologyGraph;

    @Before
    public void setUp() {
        ontologyGraph = new OntologyGraph();
    }

    private OntologyRelationship createRelationship(String child, String parent, OntologyRelationType relation) {
        return new OntologyRelationship(child, parent, relation);
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

    public class GraphLifecycleTests {
        @Test
        public void initialisedGraphContainsNothing() {
            assertThat(ontologyGraph.getVertices().size(), is(0));
            assertThat(ontologyGraph.getEdges().size(), is(0));
        }

        @Test
        public void addingRelationshipsSucceeds() {
            ontologyGraph.addRelationships(
                    asList(
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF),
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF),
                            createRelationship(goID("2"), goID("3"), CONSIDER)
                    )
            );
            assertThat(ontologyGraph.getVertices().size(), is(3));
            assertThat(ontologyGraph.getEdges().size(), is(3));
        }
    }

    public class PathTests {
        @Test(expected = IllegalArgumentException.class)
        public void findingPathsBetweenSameVertexThrowsException() {
            ontologyGraph.addRelationships(
                    asList(
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF),
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF),
                            createRelationship(goID("2"), goID("3"), OCCURS_IN)
                    )
            );

            ontologyGraph.paths(
                    goID("1"),
                    goID("1"),
                    CAPABLE_OF_PART_OF
            );
        }

        @Test
        public void findAllPathsBetween1LevelOfAncestorsViaAllRelations() {
            ontologyGraph.addRelationships(
                    asList(
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF),
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF),
                            createRelationship(goID("2"), goID("3"), OCCURS_IN)
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
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
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF),
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF),
                            createRelationship(goID("2"), goID("3"), OCCURS_IN)
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    goID("1"),
                    goID("2"),
                    CAPABLE_OF_PART_OF
            );
            System.out.println(paths);
            assertThat(paths.size(), is(1));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsViaAllRelations() {
            ontologyGraph.addRelationships(
                    asList(
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF),
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF),
                            createRelationship(goID("2"), goID("3"), OCCURS_IN)
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
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
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF),
                            createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF),
                            createRelationship(goID("2"), goID("3"), OCCURS_IN)
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    goID("1"),
                    goID("3"),
                    CAPABLE_OF_PART_OF
            );
            System.out.println(paths);
            assertThat(paths.size(), is(0));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsVia2Relations() {
            OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =
                    createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    goID("1"),
                    goID("3"),
                    CAPABLE_OF_PART_OF,
                    OCCURS_IN
            );
            System.out.println(paths);
            assertThat(paths, contains(Arrays.asList(v1_CP_v2, v2_OI_v3)));
        }
    }

    public class AncestorTests {
        @Test
        public void findAncestorsViaAllRelations() {
            OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =
                    createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OntologyRelationType.IS_A);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.ancestors(goID("1"));

            assertThat(ancestors, containsInAnyOrder(goID("1"), goID("2"), goID("3")));
        }

        @Test
        public void findAncestorsVia1Relation() {
            OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =
                    createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.ancestors(goID("1"), CAPABLE_OF_PART_OF);

            assertThat(ancestors, containsInAnyOrder(goID("1"), goID("2")));
        }

        @Test
        public void ancestorsStopForSuccessiveHasPartsInSimpleScenario() {
            OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), HAS_PART);
            OntologyRelationship v1_CP_v2 = createRelationship(goID("2"), goID("3"), HAS_PART);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2));

            Set<String> ancestors = ontologyGraph.ancestors(goID("1"));

            // only the terms related to the first relationship
            assertThat(ancestors, containsInAnyOrder(goID("1"), goID("2")));
        }

        @Test
        public void ancestorsStopForSuccessiveHasPartsInComplexScenario() {
            OntologyRelationship v1_IS_v2 = createRelationship(goID("1"), goID("2"), IS_A);
            OntologyRelationship v2_HP_v3 = createRelationship(goID("2"), goID("3"), HAS_PART);
            OntologyRelationship v3_HP_v4 = createRelationship(goID("3"), goID("4"), HAS_PART);
            OntologyRelationship v2_IS_v5 = createRelationship(goID("2"), goID("5"), IS_A);
            OntologyRelationship v5_IS_v6 = createRelationship(goID("5"), goID("6"), IS_A);
            OntologyRelationship v1_IS_v8 = createRelationship(goID("1"), goID("8"), IS_A);
            OntologyRelationship v7_IS_v8 = createRelationship(goID("7"), goID("8"), IS_A);
            OntologyRelationship v8_IS_v9 = createRelationship(goID("8"), goID("9"), IS_A);


            ontologyGraph.addRelationships(asList(
                    v1_IS_v2,
                    v2_HP_v3,
                    v3_HP_v4,
                    v2_IS_v5,
                    v5_IS_v6,
                    v1_IS_v8,
                    v7_IS_v8,
                    v8_IS_v9
            ));

            Set<String> ancestors = ontologyGraph.ancestors(goID("1"));

            // only the terms related to the first relationship
            assertThat(ancestors, containsInAnyOrder(goID("1"), goID("2"), goID("5"), goID("6"), goID("8"), goID("9")));
        }
    }

    public class DescendantTests {
        @Test
        public void findDescendantsViaAllRelations() {
            OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =
                    createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.descendants(goID("3"));

            assertThat(ancestors, containsInAnyOrder(goID("1"), goID("2")));
        }

        @Test
        public void findDescendantsVia1Relation() {
            OntologyRelationship v1_CO_v2 = createRelationship(goID("1"), goID("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =
                    createRelationship(goID("1"), goID("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(goID("2"), goID("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.descendants(goID("3"), OCCURS_IN);

            assertThat(ancestors, contains(goID("2")));
        }
    }

}