package uk.ac.ebi.quickgo.ontology.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.*;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationship.combineRelationships;

/**
 * This class tests that the composition of relationships produces the expected combined relationship.
 *
 * Created 23/05/16
 * @author Edd
 */

class OntologyRelationshipTest {

    private static void checkVerticesAreCorrect(OntologyRelationship child2Parent, OntologyRelationship
            parent2GrandParent,
            OntologyRelationship combined) {
        assertThat(combined.child, is(child2Parent.child));
        assertThat(combined.parent, is(parent2GrandParent.parent));
    }

    private static OntologyRelationship createChildParentRelationship(OntologyRelationType type) {
        return new OntologyRelationship("child", "parent", type);
    }

    private static OntologyRelationship createParentGrandParentRelationship(OntologyRelationType type) {
        return new OntologyRelationship("parent", "grandparent", type);
    }

    @Nested
    class CombinationTest {

        private OntologyRelationType childParentRelationship;
        private OntologyRelationType parentGrandParentRelationship;
        private OntologyRelationType combinedRelationship;

        void initCombinationTest(OntologyRelationType childParentRelationship, OntologyRelationType
                parentGrandParentRelationship, OntologyRelationType combinedRelationship) {
            this.childParentRelationship = childParentRelationship;
            this.parentGrandParentRelationship = parentGrandParentRelationship;
            this.combinedRelationship = combinedRelationship;
        }

        public static Collection<Object[]> data() {
            List<Object[]> testCaseParameters = new ArrayList<>();

            createRelationshipCompositionTestCases().stream().forEach(testCaseParameters::add);
            generateCombinationsResultingInUndefined().stream().forEach(testCaseParameters::add);

            return testCaseParameters;
        }

        public static Collection<Object[]> generateCombinationsResultingInUndefined() {
            List<Object[]> testCases = new ArrayList<>();
            for (OntologyRelationType relationship1 : OntologyRelationType.values()) {
                for (OntologyRelationType relationship2 : OntologyRelationType.values()) {
                    if(relationship1 != IDENTITY
                            && relationship2 != IDENTITY
                            && (relationship1 != HAS_PART || relationship2 != HAS_PART)
                            && relationship1 != IS_A
                            && relationship2 != IS_A
                            && (relationship1 != HAS_PART && relationship2 != HAS_PART)
                            && relationship1 != OCCURS_IN
                            && (relationship1 != REGULATES && relationship2 != PART_OF))
                    addTestCase(relationship1, relationship2, UNDEFINED, testCases);
                }
            }
            return testCases;
        }

        @MethodSource("data")
        @ParameterizedTest(name = "{index}: combine({0}, {1}) = {2}")
        void testCombination(OntologyRelationType childParentRelationship, OntologyRelationType
                parentGrandParentRelationship, OntologyRelationType combinedRelationship) {
            initCombinationTest(childParentRelationship, parentGrandParentRelationship, combinedRelationship);
            OntologyRelationship child2Parent = createChildParentRelationship(childParentRelationship);
            OntologyRelationship parent2GrandParent =
                    createParentGrandParentRelationship(parentGrandParentRelationship);

            OntologyRelationship combination = combineRelationships(child2Parent, parent2GrandParent);
            checkVerticesAreCorrect(child2Parent, parent2GrandParent, combination);
            assertThat(combination.relationship, is(combinedRelationship));
        }

        private static Collection<Object[]> createRelationshipCompositionTestCases() {
            List<Object[]> testCases = new ArrayList<>();

            addTestCase(IDENTITY, IDENTITY, IDENTITY, testCases);
            addTestCase(IDENTITY, IDENTITY, IDENTITY, testCases);
            addTestCase(UNDEFINED, IDENTITY, UNDEFINED, testCases);
            addTestCase(HAS_PART, NEGATIVE_REGULATES, UNDEFINED, testCases);
            addTestCase(NEGATIVE_REGULATES, HAS_PART, UNDEFINED, testCases);
            addTestCase(IS_A, NEGATIVE_REGULATES, NEGATIVE_REGULATES, testCases);
            addTestCase(NEGATIVE_REGULATES, IS_A, NEGATIVE_REGULATES, testCases);
            addTestCase(PART_OF, PART_OF, PART_OF, testCases);
            addTestCase(OCCURS_IN, PART_OF, OCCURS_IN, testCases);
            addTestCase(REGULATES, PART_OF, REGULATES, testCases);

            return testCases;
        }

        private static void addTestCase(OntologyRelationType childParent, OntologyRelationType parentGrandParent,
                OntologyRelationType combined, List<Object[]> testCases) {
            testCases.add(new Object[]{childParent, parentGrandParent, combined});
        }
    }

    @Nested
    class SetupTest {
        @Test
        void incorrectlyCombinedRelationshipsCausesException() {
            OntologyRelationship child2Parent = createChildParentRelationship(OntologyRelationType.UNDEFINED);
            OntologyRelationship parent2GrandParent = createChildParentRelationship(OntologyRelationType.UNDEFINED);
            assertThrows(IllegalArgumentException.class, () -> combineRelationships(child2Parent, parent2GrandParent));
        }
    }
}