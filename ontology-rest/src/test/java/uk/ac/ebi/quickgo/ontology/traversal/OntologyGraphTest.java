package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.*;

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
    private OntologyRelationship v1_CO_v2;
    private OntologyRelationship v1_CP_v2;
    private OntologyRelationship v2_OI_v3;
    private OntologyRelationship v2_IA_v3;

    @Before
    public void setUp() {
        ontologyGraph = new OntologyGraph();

        v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
        v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
        v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);
        v2_IA_v3 = createRelationship(id("2"), id("3"), OntologyRelationType.IS_A);
    }

    private OntologyRelationship createRelationship(String child, String parent, OntologyRelationType relation) {
        return new OntologyRelationship(child, parent, relation);
    }

    private Set<String> ids(String... ids) {
        Set<String> idSet = new HashSet<>();
        for (String id : ids) {
            idSet.add(id(id));
        }
        return idSet;
    }

    private String id(String id) {return "GO:" + id;}

    private void setupGraphWith3SimpleRelationships() {
        ontologyGraph.addRelationships(
                asList(
                        v1_CO_v2,
                        v1_CP_v2,
                        v2_OI_v3
                )
        );
    }

    public class GraphLifecycleTests {
        @Test
        public void initialisedGraphContainsNothing() {
            assertThat(ontologyGraph.getVertices().size(), is(0));
            assertThat(ontologyGraph.getEdges().size(), is(0));
        }

        @Test
        public void addingRelationshipsSucceeds() {
            setupGraphWith3SimpleRelationships();

            assertThat(ontologyGraph.getVertices().size(), is(3));
            assertThat(ontologyGraph.getEdges().size(), is(3));
        }

        @Test(expected = IllegalArgumentException.class)
        public void addingNullRelationshipsThrowsException() {
            ontologyGraph.addRelationships(null);
        }

        @Test
        public void addingEmptyRelationshipsDoesNotThrowException() {
            ontologyGraph.addRelationships(Collections.emptyList());
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsWithEmptyStartAndEndVerticesThrowsException() {
            ontologyGraph.paths(
                    Collections.emptySet(),
                    Collections.emptySet()
            );
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsWithNullStartAndEndVerticesThrowsException() {
            ontologyGraph.paths(
                    null,
                    null
            );
        }
    }

    public class PathTests {

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsBetweenSameVertexThrowsException() {
            ontologyGraph.paths(
                    ids("1"),
                    ids("1"),
                    CAPABLE_OF_PART_OF
            );
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsWithNullStartVerticesThrowsException() {
            ontologyGraph.paths(
                    null,
                    ids("1"),
                    CAPABLE_OF_PART_OF
            );
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsWithNullEndVerticesThrowsException() {
            ontologyGraph.paths(
                    ids("1"),
                    null,
                    CAPABLE_OF_PART_OF
            );
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsWithEmptyStartVerticesThrowsException() {
            ontologyGraph.paths(
                    Collections.emptySet(),
                    ids("1"),
                    CAPABLE_OF_PART_OF
            );
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingPathsWithEmptyEndVerticesThrowsException() {
            ontologyGraph.paths(
                    ids("1"),
                    Collections.emptySet(),
                    CAPABLE_OF_PART_OF
            );
        }

        @Test
        public void findAllPathsBetween1LevelOfAncestorsViaAllRelations() {
            setupGraphWith3SimpleRelationships();

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    ids("1"),
                    ids("2")
            );

            assertThat(paths, hasSize(2));
            checkPathsContains(paths, Collections.singletonList(v1_CO_v2));
            checkPathsContains(paths, Collections.singletonList(v1_CP_v2));
        }

        @Test
        public void findAllPathsBetween1LevelOfAncestorsVia1Relation() {
            setupGraphWith3SimpleRelationships();

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    ids("1"),
                    ids("2"),
                    CAPABLE_OF_PART_OF
            );

            assertThat(paths, hasSize(1));
            checkPathsContains(paths, Collections.singletonList(v1_CP_v2));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsViaAllRelations() {
            setupGraphWith3SimpleRelationships();

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    ids("1"),
                    ids("3")
            );

            assertThat(paths, hasSize(2));
            checkPathsContains(paths, Arrays.asList(v1_CO_v2, v2_OI_v3));
            checkPathsContains(paths, Arrays.asList(v1_CP_v2, v2_OI_v3));
        }

        @Test
        public void findZeroPathsBetween2LevelsOfAncestorsVia1Relation() {
            setupGraphWith3SimpleRelationships();

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    ids("1"),
                    ids("3"),
                    CAPABLE_OF_PART_OF
            );

            assertThat(paths, hasSize(0));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsVia2Relations() {
            setupGraphWith3SimpleRelationships();

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    ids("1"),
                    ids("3"),
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
                    ids("1"),
                    ids("6")
            );

            assertThat(paths, hasSize(4));
            checkPathsContains(paths, Arrays.asList(v1_IS_v2, v2_IS_v5, v5_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v8, v8_IS_v9, v9_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v7, v7_IS_v9, v9_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v7, v7_IS_v8, v8_IS_v9, v9_IS_v6));
        }

        @Test
        public void checkAllPathsExistInComplexGraphBetween2StartVerticesAnd2EndVertices() {
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
                    ids("1", "2"),
                    ids("6", "4")
            );

            assertThat(paths, hasSize(7));

            // between 1 & 6
            checkPathsContains(paths, Arrays.asList(v1_IS_v2, v2_IS_v5, v5_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v8, v8_IS_v9, v9_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v7, v7_IS_v9, v9_IS_v6));
            checkPathsContains(paths, Arrays.asList(v1_IS_v7, v7_IS_v8, v8_IS_v9, v9_IS_v6));

            // between 2 & 4
            checkPathsContains(paths, Arrays.asList(v2_HP_v3, v3_HP_v4));
            checkPathsContains(paths, Arrays.asList(v1_IS_v2, v2_HP_v3, v3_HP_v4));

            // between 2 & 6
            checkPathsContains(paths, Arrays.asList(v2_IS_v5, v5_IS_v6));
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

        @Test(expected = IllegalArgumentException.class)
        public void findingAncestorsWithNullVerticesThrowsException() {
            ontologyGraph.ancestors(null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingAncestorsWithEmptyVerticesThrowsException() {
            ontologyGraph.ancestors(Collections.emptySet());
        }

        @Test
        public void findAncestorsViaAllRelations() {
            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_IA_v3));

            List<String> ancestors = ontologyGraph.ancestors(ids("1"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("3")));
        }

        @Test
        public void findAncestorsVia1Relation() {
            setupGraphWith3SimpleRelationships();

            List<String> ancestors = ontologyGraph.ancestors(ids("1"), CAPABLE_OF_PART_OF);

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2")));
        }

        @Test
        public void ancestorsStopForSuccessiveHasPartsInSimpleScenario() {
            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), HAS_PART);
            OntologyRelationship v1_CP_v2 = createRelationship(id("2"), id("3"), HAS_PART);

            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2));

            List<String> ancestors = ontologyGraph.ancestors(ids("1"));

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

            List<String> ancestors = ontologyGraph.ancestors(ids("1"));

            // ancestors do not include those involved in
            // the transitive HAS_PART relationship
            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("5"), id("6"), id("8"), id("9")));
        }

        @Test
        public void ancestorsFrom2VerticesInComplexScenario() {
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

            List<String> ancestors = ontologyGraph.ancestors(ids("1", "7"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("5"), id("6"), id("7"), id("8"), id("9")));
        }
    }

    public class DescendantTests {
        @Test(expected = IllegalArgumentException.class)
        public void findingDescendantsWithNullVerticesThrowsException() {
            ontologyGraph.descendants(null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingDescendantsWithEmptyVerticesThrowsException() {
            ontologyGraph.descendants(Collections.emptySet());
        }

        @Test
        public void findDescendantsViaAllRelations() {
            //            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            //            OntologyRelationship v1_CP_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            //            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);
            //
            //            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));
            setupGraphWith3SimpleRelationships();

            List<String> ancestors = ontologyGraph.descendants(ids("3"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("3")));
        }

        @Test
        public void findDescendantsVia1Relation() {
            //            OntologyRelationship v1_CO_v2 = createRelationship(id("1"), id("2"), CAPABLE_OF);
            //            OntologyRelationship v1_CP_v2 =createRelationship(id("1"), id("2"), CAPABLE_OF_PART_OF);
            //            OntologyRelationship v2_OI_v3 = createRelationship(id("2"), id("3"), OCCURS_IN);
            //
            //            ontologyGraph.addRelationships(asList(v1_CO_v2, v1_CP_v2, v2_OI_v3));
            setupGraphWith3SimpleRelationships();

            List<String> ancestors = ontologyGraph.descendants(ids("3"), OCCURS_IN);

            assertThat(ancestors, containsInAnyOrder(id("3"), id("2")));
        }

        @Test
        public void findDescendantsViaAllRelationsOverComplexGraph() {
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

            List<String> ancestors = ontologyGraph.descendants(ids("4"));
            assertThat(ancestors, containsInAnyOrder(id("4"), id("3"), id("2"), id("1")));
        }

        @Test
        public void findDescendantsFrom2VerticesViaAllRelationsOverComplexGraph() {
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

            List<String> ancestors = ontologyGraph.descendants(ids("4", "8"));
            assertThat(ancestors, containsInAnyOrder(id("4"), id("3"), id("2"), id("1"), id("7"), id("8")));
        }

        @Test
        public void findDescendantsVia1RelationOverComplexGraph() {
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

            List<String> ancestors = ontologyGraph.descendants(ids("4"), HAS_PART);
            assertThat(ancestors, containsInAnyOrder(id("4"), id("3"), id("2")));
        }
    }
}