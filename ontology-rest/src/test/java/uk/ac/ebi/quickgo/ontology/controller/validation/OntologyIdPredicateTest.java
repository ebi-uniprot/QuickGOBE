package uk.ac.ebi.quickgo.ontology.controller.validation;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidECOTermId;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;

/**
 * @author Tony Wardell
 * Date: 18/10/2016
 * Time: 11:16
 * Created with IntelliJ IDEA.
 */
class OntologyIdPredicateTest {

    @Test
    void validUpperCaseGOIdentifierReturnsTrue(){
        assertThat(isValidGOTermId().test("GO:1234123"),is(true));
    }

    @Test
    void validLowercaseGOIdentifierReturnsTrue(){
        assertThat(isValidGOTermId().test("go:1234123"),is(true));
    }

    @Test
    void invalidGOIdentifierReturnsFalse(){
        assertThat(isValidGOTermId().test("GO:ABCDEFGH"),is(false));
    }

    @Test
    void validUpperCaseECOIdentifierReturnsTrue(){
        assertThat(isValidECOTermId().test("ECO:1234123"),is(true));
    }

    @Test
    void validLowercaseECOIdentifierReturnsTrue(){
        assertThat(isValidECOTermId().test("eco:1234123"),is(true));
    }

    @Test
    void invalidECOIdentifierReturnsFalse(){
        assertThat(isValidECOTermId().test("ECO:ABCDEFGH"),is(false));
    }
}