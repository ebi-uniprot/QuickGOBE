package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created 20/05/16
 * @author Edd
 */
public class OntologyRelationshipValidatorTest {

    private OntologyRelationshipValidator validator;
    private RawOntologyRelationship relationship;

    @Before
    public void setUp() {
        this.validator = new OntologyRelationshipValidator();

        relationship = createValidRelationship();
    }

    private RawOntologyRelationship createValidRelationship() {
        RawOntologyRelationship tuple = new RawOntologyRelationship();
        tuple.child = "GO:childValue";
        tuple.parent = "GO:parentValue";
        tuple.relationship = OntologyRelationType.CAPABLE_OF.getShortName();
        return tuple;
    }

    @Test
    public void findsCorrectNameSpace() {
        String nameSpace = "nameSpace";
        String separator = ":";
        String value = "value";
        String foundNameSpace = validator.nameSpaceOf(nameSpace + separator + value);
        assertThat(foundNameSpace, is(nameSpace));
    }

    @Test(expected = ValidationException.class)
    public void nodeWithMissingColonFailsToFindNameSpace() {
        String nameSpace = "nameSpace";
        String noSeparator = "";
        String value = "value";
        validator.nameSpaceOf(nameSpace + noSeparator + value);
    }

    @Test
    public void relationshipNameSpacesAreValidAsTheyTraverseSameNameSpaceAndIncludeColonsAsSeparators() {
        validator.checkValidNameSpaces(relationship);
    }

    @Test(expected = ValidationException.class)
    public void differingNameSpacesBetweenParentAndChildVerticesAreInvalid() {
        relationship.child = "GO:value";
        relationship.parent = "ECO:value";
        validator.checkValidNameSpaces(relationship);
    }

    @Test(expected = ValidationException.class)
    public void nonECOOrGONameSpacesAreInvalid() {
        relationship.child = "nameSpace1:value";
        relationship.parent = "nameSpace1:value";
        validator.checkValidNameSpaces(relationship);
    }

    @Test
    public void relationshipIsValid() {
        validator.checkValidRelationship(relationship.relationship);
    }

    @Test(expected = ValidationException.class)
    public void nonOntologyRelationshipIsInvalid() {
        relationship.relationship = "THIS_DOES_NOT_EXIST";
        validator.checkValidRelationship(relationship.relationship);
    }

    @Test
    public void fullRelationshipIsValid() throws Exception {
        OntologyRelationship validatedTuple = validator.process(this.relationship);
        assertThat(validatedTuple.child, is(relationship.child));
        assertThat(validatedTuple.parent, is(relationship.parent));
        assertThat(validatedTuple.relationship, is(OntologyRelationType.getByShortName(relationship.relationship)));
    }

    @Test(expected = ValidationException.class)
    public void fullRelationshipIsInvalid() throws Exception {
        relationship.relationship = "THIS_DOES_NOT_EXIST";
        validator.process(relationship);
    }
}