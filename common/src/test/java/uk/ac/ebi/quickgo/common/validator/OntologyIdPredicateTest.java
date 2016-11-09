package uk.ac.ebi.quickgo.common.validator;

import org.junit.Test;

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
public class OntologyIdPredicateTest {

    @Test
    public void validUpperCaseGOIdentifierReturnsTrue(){
        assertThat(isValidGOTermId().test("GO:1234123"),is(true));
    }

    @Test
    public void validLowercaseGOIdentifierReturnsTrue(){
        assertThat(isValidGOTermId().test("go:1234123"),is(true));
    }

    @Test
    public void invalidGOIdentifierReturnsFalse(){
        assertThat(isValidGOTermId().test("GO:ABCDEFGH"),is(false));
    }

    @Test
    public void validUpperCaseECOIdentifierReturnsTrue(){
        assertThat(isValidECOTermId().test("ECO:1234123"),is(true));
    }

    @Test
    public void validLowercaseECOIdentifierReturnsTrue(){
        assertThat(isValidECOTermId().test("eco:1234123"),is(true));
    }

    @Test
    public void invalidECOIdentifierReturnsFalse(){
        assertThat(isValidECOTermId().test("ECO:ABCDEFGH"),is(false));
    }
}