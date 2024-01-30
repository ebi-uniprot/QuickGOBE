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

/**
 * Validate that ontology relationships are retrieved correctly.
 *
 * Created 20/05/16
 * @author Edd
 */

class OntologyRelationTypeTest {
    /**
     * Tests the ability to lookup relationships based on their short names
     */
    @Nested
    class LookupTest {
        private static final String COMMA = ",";

        @Test
        void validRelationshipsCanBeRetrievedByShortName() {
            for (OntologyRelationType relation : OntologyRelationType.values()) {
                OntologyRelationType relationRetrieved = OntologyRelationType.getByShortName(relation.getShortName());
                assertThat(relationRetrieved, is(relation));
            }
        }

        @Test
        void validRelationshipsCanBeRetrievedByLongName() {
            for (OntologyRelationType relation : OntologyRelationType.values()) {
                OntologyRelationType relationRetrieved = OntologyRelationType.getByLongName(relation.getLongName());
                assertThat(relationRetrieved, is(relation));
            }
        }

        @Test
        void ensureDefaultTraversalTypesAreValid() {
            for (String traversalType : DEFAULT_TRAVERSAL_TYPES_CSV.split(COMMA)) {
                OntologyRelationType.getByLongName(traversalType);
            }
        }

        @Test
        void ensureGoTraversalTypesAreValid() {
            for (String traversalType : GO_GRAPH_TRAVERSAL_TYPES_CSV.split(COMMA)) {
                OntologyRelationType.getByLongName(traversalType);
            }
        }

        @Test
        void ensureEcoTraversalTypesAreValid() {
            for (String traversalType : GO_GRAPH_TRAVERSAL_TYPES_CSV.split(COMMA)) {
                OntologyRelationType.getByLongName(traversalType);
            }
        }

        @Test
        void invalidRelationshipCausesIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> OntologyRelationType.getByShortName("THIS_DOES_NOT_EXIST"));
        }
    }

    /**
     * Tests all combinations of relationship transitivity according to
     * QuickGO's interpretation of ontology relationship transitivity rules
     */
    @Nested
    class RelationshipTransitivityTests {
        private boolean startHasJoinedRelationType;
        private OntologyRelationType joiningRelation;
        private OntologyRelationType startRelation;

        void initRelationshipTransitivityTests(OntologyRelationType startRelation, OntologyRelationType
                joiningRelation, boolean startHasJoinedRelationType) {
            this.startRelation = startRelation;
            this.joiningRelation = joiningRelation;
            this.startHasJoinedRelationType = startHasJoinedRelationType;
        }

        public static Collection<Object[]> data() {
            List<Object[]> testCaseParameters = new ArrayList<>();

            for (OntologyRelationType startType : OntologyRelationType.values()) {
                for (OntologyRelationType joiningType : OntologyRelationType.values()) {
                    boolean hasTransitiveType = false;
                    if (joiningType == UNDEFINED
                            || startType == IDENTITY
                            || joiningType == startType
                            || joiningType == REGULATES &&
                            (startType == POSITIVE_REGULATES || startType == NEGATIVE_REGULATES)) {
                        hasTransitiveType = true;
                    }
                    testCaseParameters.add(new Object[]{startType, joiningType, hasTransitiveType});
                }
            }

            return testCaseParameters;
        }

        @MethodSource("data")
        @ParameterizedTest(name = "{index}: {0}.hasTransitiveType({1}) = {2}")
        void testRelationshipTransitivity(OntologyRelationType startRelation, OntologyRelationType
                joiningRelation, boolean startHasJoinedRelationType) {
            initRelationshipTransitivityTests(startRelation, joiningRelation, startHasJoinedRelationType);
            assertThat(this.startRelation.hasTransitiveType(this.joiningRelation), is(this.startHasJoinedRelationType));
        }
    }
}
