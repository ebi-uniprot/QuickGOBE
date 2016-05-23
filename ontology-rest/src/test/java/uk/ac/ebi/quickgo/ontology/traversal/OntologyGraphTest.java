package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyRelationship;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType.*;

/**
 * Validates the behaviour of {@link OntologyGraph}. The tests are divided amongst different
 * nested classes, to help with organisation of the different types of tests.
 *
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

    private String id(String value) {
        return "GO:" + value;
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
                            createRelationship(id("1"), id("2"), CAPABLE_OF),
                            createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF),
                            createRelationship(id("2"), id("3"), CONSIDER)
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
                            createRelationship(id("1"), id("2"), CAPABLE_OF),
                            createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF),
                            createRelationship(id("2"), id("3"), OCCURS_IN)
                    )
            );

            ontologyGraph.paths(
                    id("1"),
                    id("1"),
                    CAPABLE_OF_PART_OF
            );
        }

        @Test
        public void findAllPathsBetween1LevelOfAncestorsViaAllRelations() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);
            ontologyGraph.addRelationships(
                    asList(
                            v1_CO_v2,
                            v1_CP_v2,
                            v2_OI_v3
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    id("1"),
                    id("2")
            );

            assertThat(paths, hasSize(2));
            checkPathsContains(paths, Collections.singletonList(v1_CO_v2));
            checkPathsContains(paths, Collections.singletonList(v1_CP_v2));
        }

        @Test
        public void findAllPathsBetween1LevelOfAncestorsVia1Relation() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);
            ontologyGraph.addRelationships(
                    asList(
                            v1_CO_v2,
                            v1_CP_v2,
                            v2_OI_v3
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    id("1"),
                    id("2"),
                    CAPABLE_OF_PART_OF
            );

            assertThat(paths, hasSize(1));
            checkPathsContains(paths, Collections.singletonList(v1_CP_v2));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsViaAllRelations() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);
            ontologyGraph.addRelationships(
                    asList(
                            v1_CO_v2,
                            v1_CP_v2,
                            v2_OI_v3
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    id("1"),
                    id("3")
            );

            assertThat(paths, hasSize(2));
            checkPathsContains(paths, Arrays.asList(v1_CO_v2, v2_OI_v3));
            checkPathsContains(paths, Arrays.asList(v1_CP_v2, v2_OI_v3));
        }

        @Test
        public void findZeroPathsBetween2LevelsOfAncestorsVia1Relation() {
            ontologyGraph.addRelationships(
                    asList(
                            createRelationship(id("1"), id("2"), CAPABLE_OF),
                            createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF),
                            createRelationship(id("2"), id("3"), OCCURS_IN)
                    )
            );

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    id("1"),
                    id("3"),
                    CAPABLE_OF_PART_OF
            );

            assertThat(paths, hasSize(0));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsVia2Relations() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    id("1"),
                    id("3"),
                    CAPABLE_OF_PART_OF,
                    OCCURS_IN
            );

            assertThat(paths, hasSize(1));
            checkPathsContains(paths, Arrays.asList(v1_CP_v2, v2_OI_v3));
        }

        @Test
        public void checkAllPathsExistInComplexGraph() {
            OntologyRelationship v1_IS_v2 = createRelationship(id("1"), id("2"), IS_A);
            OntologyRelationship v2_HP_v3 = createRelationship(id("2"), id("3"), HAS_PART);
            OntologyRelationship v3_HP_v4 = createRelationship(id("3"), id("4"), HAS_PART);
            OntologyRelationship v2_IS_v5 = createRelationship(id("2"), id("5"), IS_A);
            OntologyRelationship v5_IS_v6 = createRelationship(id("5"), id("6"), IS_A);
            OntologyRelationship v1_IS_v8 = createRelationship(id("1"), id("8"), IS_A);
            OntologyRelationship v7_IS_v8 = createRelationship(id("7"), id("8"), IS_A);
            OntologyRelationship v1_IS_v7 = createRelationship(id("1"), id("7"), IS_A);
            OntologyRelationship v8_IS_v9 = createRelationship(id("8"), id("9"), IS_A);
            OntologyRelationship v7_IS_v9 = createRelationship(id("7"), id("9"), IS_A);
            OntologyRelationship v9_IS_v6 = createRelationship(id("9"), id("6"), IS_A);

            ontologyGraph.addRelationships(asList(
                    v1_IS_v2,
                    v2_HP_v3,
                    v3_HP_v4,
                    v2_IS_v5,
                    v5_IS_v6,
                    v1_IS_v8,
                    v7_IS_v8,
                    v1_IS_v7,
                    v8_IS_v9,
                    v7_IS_v9,
                    v9_IS_v6
            ));

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    id("1"),
                    id("6")
            );

            assertThat(paths, hasSize(4));
            checkPathsContains(paths, Arrays.asList(v1_IS_v2, v2_IS_v5, v5_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v8, v8_IS_v9, v9_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v7, v7_IS_v9, v9_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v7, v7_IS_v8, v8_IS_v9, v9_IS_v6));
        }

        private void checkPathsContains(
                List<List<OntologyRelationship>> paths,
                List<OntologyRelationship> ontologyRelationships) {
            if (!paths.contains(ontologyRelationships)) {
                throw new AssertionError("Expected path: " + ontologyRelationships + " in path list: " + paths);
            }
        }
    }

    public class AncestorTests {
        @Test
        public void findAncestorsViaAllRelations() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OntologyRelationType.IS_A);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.ancestors(id("1"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("3")));
        }

        @Test
        public void findAncestorsVia1Relation() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.ancestors(id("1"), CAPABLE_OF_PART_OF);

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2")));
        }

        @Test
        public void ancestorsStopForSuccessiveHasPartsInSimpleScenario() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), HAS_PART);
            OntologyRelationship v1_CP_v2 = createRelationship(id("2"), id("3"), HAS_PART);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2));

            Set<String> ancestors = ontologyGraph.ancestors(id("1"));

            // only the terms related to the first relationship
            assertThat(ancestors, containsInAnyOrder(id("1"), id("2")));
        }

        @Test
        public void ancestorsStopForSuccessiveHasPartsInComplexScenario() {
            OntologyRelationship v1_IS_v2 = createRelationship(id("1"), id("2"), IS_A);
            OntologyRelationship v2_HP_v3 = createRelationship(id("2"), id("3"), HAS_PART);
            OntologyRelationship v3_HP_v4 = createRelationship(id("3"), id("4"), HAS_PART);
            OntologyRelationship v2_IS_v5 = createRelationship(id("2"), id("5"), IS_A);
            OntologyRelationship v5_IS_v6 = createRelationship(id("5"), id("6"), IS_A);
            OntologyRelationship v1_IS_v8 = createRelationship(id("1"), id("8"), IS_A);
            OntologyRelationship v7_IS_v8 = createRelationship(id("7"), id("8"), IS_A);
            OntologyRelationship v8_IS_v9 = createRelationship(id("8"), id("9"), IS_A);

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

            Set<String> ancestors = ontologyGraph.ancestors(id("1"));

            // ancestors do not include those involved in the
            // transitive HAS_PART relationship
            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("5"), id("6"), id("8"), id("9")));
        }
    }

    public class DescendantTests {
        @Test
        public void findDescendantsViaAllRelations() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =
                    createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.descendants(id("3"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2")));
        }

        @Test
        public void findDescendantsVia1Relation() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            OntologyRelationship v1_CP_v2 =createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));

            Set<String> ancestors = ontologyGraph.descendants(id("3"), OCCURS_IN);

            assertThat(ancestors, contains(id("2")));
        }
    }

}