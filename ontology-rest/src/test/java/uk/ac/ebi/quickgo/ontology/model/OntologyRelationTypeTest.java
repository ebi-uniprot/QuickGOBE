package uk.ac.ebi.quickgo.ontology.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.*;

/**
 * Validate that ontology relationships are retrieved correctly.
 *
 * Created 20/05/16
 * @author Edd
 */
@RunWith(Enclosed.class)
public class OntologyRelationTypeTest {
    /**
     * Tests the ability to lookup relationships based on their short names
     */
    public static class LookupTest {
        private static final String COMMA = ",";

        @Test
        public void validRelationshipsCanBeRetrievedByShortName() {
            for (OntologyRelationType relation : OntologyRelationType.values()) {
                OntologyRelationType relationRetrieved = OntologyRelationType.getByShortName(relation.getShortName());
                assertThat(relationRetrieved, is(relation));
            }
        }

        @Test
        public void validRelationshipsCanBeRetrievedByLongName() {
            for (OntologyRelationType relation : OntologyRelationType.values()) {
                OntologyRelationType relationRetrieved = OntologyRelationType.getByLongName(relation.getLongName());
                assertThat(relationRetrieved, is(relation));
            }
        }

        @Test
        public void ensureDefaultTraversalTypesAreValid() {
            for (String traversalType : DEFAULT_TRAVERSAL_TYPES_CSV.split(COMMA)) {
                OntologyRelationType.getByLongName(traversalType);
            }
        }

        @Test
        public void ensureGoTraversalTypesAreValid() {
            for (String traversalType : GO_GRAPH_TRAVERSAL_TYPES_CSV.split(COMMA)) {
                OntologyRelationType.getByLongName(traversalType);
            }
        }

        @Test
        public void ensureEcoTraversalTypesAreValid() {
            for (String traversalType : GO_GRAPH_TRAVERSAL_TYPES_CSV.split(COMMA)) {
                OntologyRelationType.getByLongName(traversalType);
            }
        }

        @Test(expected = IllegalArgumentException.class)
        public void invalidRelationshipCausesIllegalArgumentException() {
            OntologyRelationType.getByShortName("THIS_DOES_NOT_EXIST");
        }
    }

    /**
     * Tests all combinations of relationship transitivity according to
     * QuickGO's interpretation of ontology relationship transitivity rules
     */
    @RunWith(Parameterized.class)
    public static class RelationshipTransitivityTests {
        private final boolean startHasJoinedRelationType;
        private final OntologyRelationType joiningRelation;
        private final OntologyRelationType startRelation;

        public RelationshipTransitivityTests(OntologyRelationType startRelation, OntologyRelationType
                joiningRelation, boolean startHasJoinedRelationType) {
            this.startRelation = startRelation;
            this.joiningRelation = joiningRelation;
            this.startHasJoinedRelationType = startHasJoinedRelationType;
        }

        @Parameterized.Parameters(name = "{index}: {0}.hasTransitiveType({1}) = {2}")
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

        @Test
        public void testRelationshipTransitivity() {
            assertThat(this.startRelation.hasTransitiveType(this.joiningRelation), is(this.startHasJoinedRelationType));
        }
    }
}
