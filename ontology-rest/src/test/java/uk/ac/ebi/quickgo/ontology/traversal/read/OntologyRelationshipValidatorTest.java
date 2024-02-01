package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 20/05/16
 * @author Edd
 */
class OntologyRelationshipValidatorTest {

    private OntologyRelationshipValidator validator;
    private RawOntologyRelationship relationship;

    @BeforeEach
    void setUp() {
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
    void findsCorrectNameSpace() {
        String nameSpace = "nameSpace";
        String separator = ":";
        String value = "value";
        String foundNameSpace = validator.nameSpaceOf(nameSpace + separator + value);
        assertThat(foundNameSpace, is(nameSpace));
    }

    @Test
    void nodeWithMissingColonFailsToFindNameSpace() {
        String nameSpace = "nameSpace";
        String noSeparator = "";
        String value = "value";
        assertThrows(ValidationException.class, () -> validator.nameSpaceOf(nameSpace + noSeparator + value));
    }

    @Test
    void relationshipNameSpacesAreValidAsTheyTraverseSameNameSpaceAndIncludeColonsAsSeparators() {
        validator.checkValidNameSpaces(relationship);
    }

    @Test
    void differingNameSpacesBetweenParentAndChildVerticesAreInvalid() {
        relationship.child = "GO:value";
        relationship.parent = "ECO:value";
        assertThrows(ValidationException.class, () -> validator.checkValidNameSpaces(relationship));
    }

    @Test
    void nonECOOrGONameSpacesAreInvalid() {
        relationship.child = "nameSpace1:value";
        relationship.parent = "nameSpace1:value";
        assertThrows(ValidationException.class, () -> validator.checkValidNameSpaces(relationship));
    }

    @Test
    void relationshipIsValid() {
        validator.checkValidRelationship(relationship.relationship);
    }

    @Test
    void nonOntologyRelationshipIsInvalid() {
        relationship.relationship = "THIS_DOES_NOT_EXIST";
        assertThrows(ValidationException.class, () -> validator.checkValidRelationship(relationship.relationship));
    }

    @Test
    void fullRelationshipIsValid() throws Exception {
        OntologyRelationship validatedTuple = validator.process(this.relationship);
        assertThat(validatedTuple.child, is(relationship.child));
        assertThat(validatedTuple.parent, is(relationship.parent));
        assertThat(validatedTuple.relationship, is(OntologyRelationType.getByShortName(relationship.relationship)));
    }

    @Test
    void fullRelationshipIsInvalidBecauseRelationshipDoesNotExist() {
        relationship.relationship = "THIS_DOES_NOT_EXIST";
        assertThrows(ValidationException.class, () -> validator.process(relationship));
    }

    @Test
    void fullRelationshipIsInvalidBecauseNameSpacesDoNotMatch() {
        relationship.child = "GO:000001";
        relationship.parent = "ECO:000001";
        assertThrows(ValidationException.class, () -> validator.process(relationship));
    }

    @Test
    void fullRelationshipIsInvalidBecauseNameSpacesDoNotExist() {
        relationship.child = "DOESNT_EXIST:000001";
        relationship.parent = "DOESNT_EXIST:000002";
        assertThrows(ValidationException.class, () -> validator.process(relationship));
    }
}