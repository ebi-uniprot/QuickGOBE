package uk.ac.ebi.quickgo.ontology.traversal;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Validate that ontology relationships are retrieved correctly.
 *
 * Created 20/05/16
 * @author Edd
 */
public class OntologyRelationTypeTest {
    @Test
    public void validRelationshipsCanBeRetrieved() {
        for (OntologyRelationType relation : OntologyRelationType.values()) {
            OntologyRelationType relationRetrieved = OntologyRelationType.getByName(relation.getShortName());
            assertThat(relationRetrieved, is(relation));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRelationshipCausesIllegalArgumentException() {
        OntologyRelationType.getByName("THIS_DOES_NOT_EXIST");
    }
}