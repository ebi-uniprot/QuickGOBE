package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorEdge;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraph;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.*;
import java.util.stream.Collectors;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.*;
import static uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph.BIOLOGICAL_PROCESS_STOP_NODE;
import static uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph.CELLULAR_COMPONENT_STOP_NODE;
import static uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph.MOLECULAR_FUNCTION_STOP_NODE;

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
        return Arrays.stream(ids)
                     .map(this::id)
                     .collect(Collectors.toSet());
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

        @Test
        public void canFetchCategorisedVertices() {
            setupGraphWith3SimpleRelationships();

            Set<String> goVertices = ontologyGraph.getVertices(OntologyType.GO);
            assertThat(goVertices, containsInAnyOrder(id("1"), id("2"), id("3")));
        }

        @Test(expected = IllegalArgumentException.class)
        public void uncategorisedVerticesCausesException() {
            setupGraphWith3SimpleRelationships();

            ontologyGraph.getVertices(OntologyType.ECO);
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
            checkPathsContains(paths, singletonList(v1_CO_v2));
            checkPathsContains(paths, singletonList(v1_CP_v2));
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
            checkPathsContains(paths, singletonList(v1_CP_v2));
        }

        @Test
        public void findAllPathsBetween2LevelsOfAncestorsViaAllRelations() {
            setupGraphWith3SimpleRelationships();

            List<List<OntologyRelationship>> paths = ontologyGraph.paths(
                    ids("1"),
                    ids("3")
            );

            assertThat(paths, hasSize(2));
            checkPathsContains(paths, asList(v1_CO_v2, v2_OI_v3));
            checkPathsContains(paths, asList(v1_CP_v2, v2_OI_v3));
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
            checkPathsContains(paths, asList(v1_CP_v2, v2_OI_v3));
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
            checkPathsContains(paths, asList(v1_IS_v2, v2_IS_v5, v5_IS_v6));
            checkPathsContains(paths, asList(v1_IS_v8, v8_IS_v9, v9_IS_v6));
            checkPathsContains(paths, asList(v1_IS_v7, v7_IS_v9, v9_IS_v6));
            checkPathsContains(paths, asList(v1_IS_v7, v7_IS_v8, v8_IS_v9, v9_IS_v6));
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
            checkPathsContains(paths, asList(v1_IS_v2, v2_IS_v5, v5_IS_v6));
            checkPathsContains(paths, asList(v1_IS_v8, v8_IS_v9, v9_IS_v6));
            checkPathsContains(paths, asList(v1_IS_v7, v7_IS_v9, v9_IS_v6));
            checkPathsContains(paths, asList(v1_IS_v7, v7_IS_v8, v8_IS_v9, v9_IS_v6));

            // between 2 & 4
            checkPathsContains(paths, asList(v2_HP_v3, v3_HP_v4));
            checkPathsContains(paths, asList(v1_IS_v2, v2_HP_v3, v3_HP_v4));

            // between 2 & 6
            checkPathsContains(paths, asList(v2_IS_v5, v5_IS_v6));
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

        @Test
        public void omitAncestorsOfOneStopNodeSuccessor() {
            String stopA = MOLECULAR_FUNCTION_STOP_NODE;
            String rootNodeThatShouldBeHidden = id("0009999");

            OntologyRelationship v1_IS_v2 = createRelationship(id("1"), id("2"), IS_A);
            OntologyRelationship v2_IS_vStopA = createRelationship(id("2"), stopA, IS_A);
            OntologyRelationship vStopA_IS_v9999 = createRelationship(stopA, rootNodeThatShouldBeHidden, IS_A);
            OntologyRelationship v1_IS_v3 = createRelationship(id("1"), id("3"), IS_A);
            OntologyRelationship v1_IS_v4 = createRelationship(id("1"), id("4"), IS_A);
            OntologyRelationship v3_IS_v5 = createRelationship(id("3"), id("5"), IS_A);
            OntologyRelationship v4_IS_v6 = createRelationship(id("4"), id("6"), IS_A);

            ontologyGraph.addRelationships(asList(
                    v1_IS_v2,
                    v2_IS_vStopA,
                    vStopA_IS_v9999,
                    v1_IS_v3,
                    v1_IS_v4,
                    v3_IS_v5,
                    v4_IS_v6
            ));

            List<String> ancestors = ontologyGraph.ancestors(ids("1"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("3"), id("4"), id("5"), id("6"), stopA));
        }

        @Test
        public void omitAncestorsOfAllStopNodeSuccessors() {
            String stopA = MOLECULAR_FUNCTION_STOP_NODE;
            String stopB = BIOLOGICAL_PROCESS_STOP_NODE;
            String stopC = CELLULAR_COMPONENT_STOP_NODE;
            String rootNodeThatShouldBeHidden = id("0009999");

            OntologyRelationship v1_IS_v2 = createRelationship(id("1"), id("2"), IS_A);
            OntologyRelationship v1_IS_v3 = createRelationship(id("1"), id("3"), IS_A);
            OntologyRelationship v1_IS_v4 = createRelationship(id("1"), id("4"), IS_A);
            OntologyRelationship v2_IS_vStopA = createRelationship(id("2"), stopA, IS_A);
            OntologyRelationship v3_IS_vStopB = createRelationship(id("2"), stopB, IS_A);
            OntologyRelationship v4_IS_vStopC = createRelationship(id("2"), stopC, IS_A);
            OntologyRelationship vStopA_IS_v9999 = createRelationship(stopA, rootNodeThatShouldBeHidden, IS_A);
            OntologyRelationship vStopB_IS_v9999 = createRelationship(stopB, rootNodeThatShouldBeHidden, IS_A);
            OntologyRelationship vStopC_IS_v9999 = createRelationship(stopC, rootNodeThatShouldBeHidden, IS_A);

            ontologyGraph.addRelationships(asList(
                    v1_IS_v2,
                    v1_IS_v3,
                    v1_IS_v4,
                    v2_IS_vStopA,
                    v3_IS_vStopB,
                    v4_IS_vStopC,
                    vStopA_IS_v9999,
                    vStopB_IS_v9999,
                    vStopC_IS_v9999
            ));

            List<String> ancestors = ontologyGraph.ancestors(ids("1"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("3"), id("4"), stopA, stopB, stopC));
        }

        @Test
        public void fetchingFilteredAncestorsAsBitSetSucceeds() {
            OntologyRelationship v0_IS_v1 = createRelationship(id("0"), id("1"), IS_A);
            OntologyRelationship v1_IS_v2 = createRelationship(id("1"), id("2"), IS_A);
            OntologyRelationship v1_HAS_v3 = createRelationship(id("1"), id("3"), HAS_PART);
            OntologyRelationship v2_PART_v4 = createRelationship(id("2"), id("4"), PART_OF);

            ontologyGraph.addRelationships(asList(v0_IS_v1, v1_IS_v2, v1_HAS_v3, v2_PART_v4));

            BitSet ancestorsBitSet =
                    ontologyGraph.getAncestorsBitSet(id("0"), asList(id("0"), id("1"), id("2")), IS_A, HAS_PART);
            for (int i = 0; i < ancestorsBitSet.size(); i++) {
                if (i == 0 || i == 1 || i == 2) {
                    assertThat(ancestorsBitSet.get(i), is(true));
                } else {
                    assertThat(ancestorsBitSet.get(i), is(false));
                }
            }
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
            setupGraphWith3SimpleRelationships();

            List<String> ancestors = ontologyGraph.descendants(ids("3"));

            assertThat(ancestors, containsInAnyOrder(id("1"), id("2"), id("3")));
        }

        @Test
        public void findDescendantsVia1Relation() {
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

    public class ChildrenTests {
        private final String parentId = id("1");

        private final OntologyRelationship grandParentIsA = createRelationship(parentId, id("0"), IS_A);
        private final OntologyRelationship childIsA = createRelationship(id("2"), parentId, IS_A);
        private final OntologyRelationship childHasPart1 = createRelationship(id("3"), parentId, HAS_PART);
        private final OntologyRelationship childHasPart2 = createRelationship(id("4"), parentId, HAS_PART);
        private final OntologyRelationship childRegulates = createRelationship(id("5"), parentId, REGULATES);
        private final OntologyRelationship grandChildIsA = createRelationship(id("6"), id("2"), IS_A);

        @Before
        public void populateOntology() {
            ontologyGraph.addRelationships(asList(
                    grandParentIsA,
                    childIsA,
                    childHasPart1,
                    childHasPart2,
                    childRegulates,
                    grandChildIsA
            ));
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingChildrenWithANullVertexThrowsException() throws Exception {
            ontologyGraph.children(null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingChildrenWithAEmptyVertexThrowsException() throws Exception {
            ontologyGraph.children("");
        }

        @Test
        public void finds1IsAChildOfParent1() throws Exception {
            Set<OntologyRelationship> expectedChildren = ontologyGraph.children(parentId, IS_A);

            assertThat(expectedChildren, contains(childIsA));
        }

        @Test
        public void finds2HasPartChildrenOfParent1() throws Exception {
            Set<OntologyRelationship> expectedChildren = ontologyGraph.children(parentId, HAS_PART);

            assertThat(expectedChildren, containsInAnyOrder(childHasPart1, childHasPart2));
        }

        @Test
        public void findsAllChildrenOfParent1WithoutAnyRelationshipsSetAsArguments() throws Exception {
            Set<OntologyRelationship> expectedChildren = ontologyGraph.children(parentId);

            assertThat(expectedChildren, containsInAnyOrder(childHasPart1, childHasPart2, childIsA, childRegulates));
        }

        @Test
        public void doesNotFindGrandParentOfParentWhenSearchingForChildren() throws Exception {
            Set<OntologyRelationship> expectedChildren = ontologyGraph.children(parentId);

            assertThat(expectedChildren, hasSize(4));
            assertThat(expectedChildren, not(hasItem(grandParentIsA)));
        }

        @Test
        public void doesNotFindGrandChildOfParentWhenSearchingForChildren() throws Exception {
            Set<OntologyRelationship> expectedChildren = ontologyGraph.children(parentId);

            assertThat(expectedChildren, hasSize(4));
            assertThat(expectedChildren, not(hasItem(grandChildIsA)));
        }
    }

    public class ParentTests {
        private final String childId = id("1");

        private final OntologyRelationship parentIsA = createRelationship(childId, id("2"), IS_A);
        private final OntologyRelationship parentHasPart1 = createRelationship(childId, id("3"), HAS_PART);
        private final OntologyRelationship parentHasPart2 = createRelationship(childId, id("4"), HAS_PART);
        private final OntologyRelationship parentRegulates = createRelationship(childId, id("5"), REGULATES);
        private final OntologyRelationship childIsA = createRelationship(id("6"), childId, REGULATES);
        private final OntologyRelationship grandParentHasPart = createRelationship(id("7"), id("3"), REGULATES);

        @Before
        public void populateOntology() {
            ontologyGraph.addRelationships(asList(
                    parentIsA,
                    parentHasPart1,
                    parentHasPart2,
                    parentRegulates,
                    childIsA,
                    grandParentHasPart
            ));
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingParentsWithANullVertexThrowsException() throws Exception {
            ontologyGraph.parents(null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void findingParentsWithAnEmptyVertexThrowsException() throws Exception {
            ontologyGraph.parents("");
        }

        @Test
        public void finds1IsAParentOfChild1() throws Exception {
            Set<OntologyRelationship> expectedParents = ontologyGraph.parents(childId, IS_A);

            assertThat(expectedParents, contains(parentIsA));
        }

        @Test
        public void finds2HasPartParentsOfChild1() throws Exception {
            Set<OntologyRelationship> expectedParents = ontologyGraph.parents(childId, HAS_PART);

            assertThat(expectedParents, containsInAnyOrder(parentHasPart1, parentHasPart2));
        }

        @Test
        public void findsAllParentsOfChild1WithoutAnyRelationshipsSetAsArguments() throws Exception {
            Set<OntologyRelationship> expectedParents = ontologyGraph.parents(childId);

            assertThat(expectedParents, containsInAnyOrder(parentIsA, parentHasPart1, parentHasPart2, parentRegulates));
        }

        @Test
        public void doesNotFindChildOfChild1WhenSearchingForParents() throws Exception {
            Set<OntologyRelationship> expectedParents = ontologyGraph.parents(childId);

            assertThat(expectedParents, hasSize(4));
            assertThat(expectedParents, not(hasItem(childIsA)));
        }

        @Test
        public void doesNotFinGrandParentOfChildWhenSearchingForParents() throws Exception {
            Set<OntologyRelationship> expectedParents = ontologyGraph.parents(childId);

            assertThat(expectedParents, hasSize(4));
            assertThat(expectedParents, not(hasItem(grandParentHasPart)));
        }
    }

    public class AncestorGraphTests {
        private OntologyGraph og;

        final OntologyVertex pyrophosphataseActivity =  new OntologyVertex("GO:0016462", "pyrophosphatase activity");
        final OntologyVertex cyclaseActivity =  new OntologyVertex("GO:0009975", "cyclase activity");
        final OntologyVertex catalyticActivity = new OntologyVertex("GO:0003824", "catalytic activity");
        final OntologyVertex molecularFunction = new OntologyVertex("GO:0003674", "molecularFunction");

        private final OntologyRelationship py_IA_cy = new OntologyRelationship(pyrophosphataseActivity.id, cyclaseActivity.id,
                                                                               IS_A);
        private final OntologyRelationship cy_IA_ca = new OntologyRelationship(cyclaseActivity.id, catalyticActivity.id,
                                                                               IS_A);
        private final OntologyRelationship ca_IA_mf = new OntologyRelationship(catalyticActivity.id, molecularFunction.id, IS_A);

        final OntologyRelationType[] relations = GO_GRAPH_TRAVERSAL_TYPES.toArray(new

                                                                                                  OntologyRelationType[]{});
        // Defaults.
        final HashSet<String> startingVertices = new HashSet<>();
        final Set<String> stopVertices = new HashSet<>();

        @Before
        public void setup(){
            og = new OntologyGraph();
            og.addRelationships(asList(py_IA_cy, cy_IA_ca, ca_IA_mf));
        }

        @Test
        public void findSubGraphUsingAllRelationsDefaultStopVertices() {
            final HashSet<String> startingVertices = new HashSet<>(singletonList(catalyticActivity.id));
            final Set<String> stopVertices = new HashSet<>();

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(2));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(catalyticActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(1));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(ca_IA_mf)));
        }

        @Test
        public void findSubGraphUsingAllRelationsSpecifyDefaultStopVertices() {
            final HashSet<String> startingVertices = new HashSet<>(singletonList(catalyticActivity.id));
            final HashSet<String> stopVertices = new HashSet<>(singletonList(molecularFunction.id));

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(2));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(catalyticActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(1));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(ca_IA_mf)));
        }

        @Test
        public void findSubGraphSpecifyingRelationsDoNotSpecifyStopNode() {
            final HashSet<String> startingVertices = new HashSet<>(singletonList(catalyticActivity.id));
            final OntologyRelationType[] relations = {OntologyRelationType.IS_A};

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(2));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(catalyticActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(1));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(ca_IA_mf)));
        }

        @Test
        public void findSubGraphSpecifyingRelationsSpecifyNonRootStopNode() {
            final HashSet<String> startingVertices = new HashSet<>(singletonList(pyrophosphataseActivity.id));
            final HashSet<String> stopVertices = new HashSet<>(singletonList(catalyticActivity.id));
            final OntologyRelationType[] relations = {OntologyRelationType.IS_A};

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(3));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity.id, cyclaseActivity.id,
                                                                  catalyticActivity.id));
            assertThat(ancestorGraph.edges, hasSize(2));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_IA_cy), toAE(cy_IA_ca)));
        }

        @Test
        public void findSubGraphWhereMultipleRouteOfSameType() {
            OntologyRelationship py_IA_mf = new OntologyRelationship(pyrophosphataseActivity.id, molecularFunction.id, IS_A);
            calculateGraphForRelationship(py_IA_mf);
        }

        @Test
        public void findSubGraphForMultipleInheritance() {
            OntologyRelationship py_RT_mf = new OntologyRelationship(pyrophosphataseActivity.id, molecularFunction
                    .id, OCCURS_IN);
            calculateGraphForRelationship(py_RT_mf);
        }

        @Test
        public void findSubGraphFilteringByRelationship() {
            OntologyRelationship py_OI_mf = new OntologyRelationship(pyrophosphataseActivity.id, molecularFunction
                    .id, OCCURS_IN);
            og.addRelationships(singletonList(py_OI_mf));
            final HashSet<String> startingVertices = new HashSet<>(singletonList(pyrophosphataseActivity.id));
            final OntologyRelationType[] relations = {OCCURS_IN};

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(2));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(1));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_OI_mf)));
        }

        @Test
        public void unrelatedVerticesIgnored() {
            //Build new graph and add to relationships
            OntologyVertex biologicalProcess = new OntologyVertex("GO:0008150","biological process");
            OntologyVertex localization = new OntologyVertex("GO:0051179","localization");
            OntologyVertex estOfLocalization = new OntologyVertex("GO:0051234","establishment of localization");
            OntologyRelationship lo_IA_bp = new OntologyRelationship(localization.id, biologicalProcess.id, IS_A);
            OntologyRelationship el_IA_lo = new OntologyRelationship(estOfLocalization.id, localization.id, IS_A);
            og.addRelationships(asList(lo_IA_bp, el_IA_lo));

            HashSet<String> startingVertices = new HashSet<>(singletonList(pyrophosphataseActivity.id));
            final OntologyRelationType[] relations = {IS_A};

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(4));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity.id, cyclaseActivity.id,
                                                                  catalyticActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(3));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_IA_cy), toAE(cy_IA_ca), toAE(ca_IA_mf)));

            //Now try un-related
            startingVertices = new HashSet<>(singletonList(estOfLocalization.id));
            ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(3));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(estOfLocalization.id, localization.id,
                                                                  biologicalProcess.id));
            assertThat(ancestorGraph.edges, hasSize(2));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(lo_IA_bp), toAE(el_IA_lo)));
        }

        @Test
        public void noMatchingSubGraphProducesEmptyAncestorGraph() {
            final HashSet<String> startingVertices = new HashSet<>(singletonList("GO:XXXXXX"));

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(0));
            assertThat(ancestorGraph.edges, hasSize(0));
        }


        @Test
        public void okIfStartingNodeIsTopNode() {
            final HashSet<String> startingVertices = new HashSet<>(singletonList(molecularFunction.id));

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(1));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(0));
        }

        @Test
        public void cyclicalOntologyCanBeCalculatedWithoutEnteringInfiniteLooping() {
            OntologyRelationship mf_OI_py = new OntologyRelationship(molecularFunction.id,
                                                                     pyrophosphataseActivity.id, OCCURS_IN);
            og.addRelationships(singletonList(mf_OI_py));
            final HashSet<String> startingVertices = new HashSet<>(singletonList(pyrophosphataseActivity.id));

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(4));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity.id, cyclaseActivity.id,
                                                                  catalyticActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(3));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_IA_cy), toAE(cy_IA_ca), toAE(ca_IA_mf)));
        }

        @Test
        public void findSubGraphForWhereRelationshipsCanBeCyclical() {
            OntologyRelationship ca_OI_py = new OntologyRelationship(catalyticActivity.id,
                                                                     pyrophosphataseActivity.id, OCCURS_IN);
            calculateGraphForRelationship(ca_OI_py);
        }

        @Test(expected = IllegalArgumentException.class)
        public void requestingSubGraphWithNullStartingVerticesThrowsException() {
            og.subGraph(null, stopVertices, relations);

        }

        @Test(expected = IllegalArgumentException.class)
        public void requestingSubGraphWithEmptyStartingVerticesThrowsException() {
            og.subGraph(startingVertices, stopVertices, relations);

        }

        @Test(expected = IllegalArgumentException.class)
        public void requestingSubGraphWithNullRelationsThrowsException() {
            og.subGraph(startingVertices, stopVertices, (OntologyRelationType[]) null);

        }

        @Test(expected = IllegalArgumentException.class)
        public void requestingSubGraphWithEmptyRelationsThrowsException() {
            final OntologyRelationType[] relations = {};

            og.subGraph(startingVertices, stopVertices, relations);

        }

        public void calculateGraphForRelationship(OntologyRelationship ontologyRelationship ) {
            og.addRelationships(singletonList(ontologyRelationship));
            final HashSet<String> startingVertices = new HashSet<>(singletonList(pyrophosphataseActivity.id));

            AncestorGraph<String> ancestorGraph = og.subGraph(startingVertices, stopVertices, relations);

            assertThat(ancestorGraph.vertices, hasSize(4));
            assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity.id, cyclaseActivity.id,
                                                                  catalyticActivity.id, molecularFunction.id));
            assertThat(ancestorGraph.edges, hasSize(4));
            assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_IA_cy), toAE(cy_IA_ca), toAE(ca_IA_mf),
                                                               toAE(ontologyRelationship)));
        }

        private class OntologyVertex {
            final String id;
            final String name;

            OntologyVertex(String id, String name) {
                this.id = id;
                this.name = name;
            }
        }

        private AncestorEdge toAE(OntologyRelationship or){
            return new AncestorEdge(or.child, or.relationship.getLongName(), or.parent);
        }
    }
}
