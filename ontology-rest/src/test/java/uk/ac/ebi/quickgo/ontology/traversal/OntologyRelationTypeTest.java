package uk.ac.ebi.quickgo.ontology.traversal;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES_CSV;

/**
 * Validate that ontology relationships are retrieved correctly.
 *
 * Created 20/05/16
 * @author Edd
 */
public class OntologyRelationTypeTest {

    private static final String COMMA = ",";

    @Test
    public void validRelationshipsCanBeRetrievedByShortName() {
        for (OntologyRelationType relation : OntologyRelationType.values()) {
            OntologyRelationType relationRetrieved = OntologyRelationType.getByName(relation.getShortName());
            assertThat(relationRetrieved, is(relation));
        }
    }

    @Test
    public void validRelationshipsCanBeRetrievedByLongName() {
        for (OntologyRelationType relation : OntologyRelationType.values()) {
            OntologyRelationType relationRetrieved = OntologyRelationType.getByName(relation.getLongName());
            assertThat(relationRetrieved, is(relation));
        }
    }

    @Test
    public void ensureDefaultTraversalTypesAreValid() {
        for (String traversalType : DEFAULT_TRAVERSAL_TYPES_CSV.split(COMMA)) {
            OntologyRelationType.getByName(traversalType);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRelationshipCausesIllegalArgumentException() {
        OntologyRelationType.getByName("THIS_DOES_NOT_EXIST");
    }
}