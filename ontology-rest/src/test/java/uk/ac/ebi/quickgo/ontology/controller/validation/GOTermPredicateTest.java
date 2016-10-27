package uk.ac.ebi.quickgo.ontology.controller.validation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.ontology.controller.validation.GOTermPredicate.isValidGOTermId;

/**
 * @author Tony Wardell
 * Date: 18/10/2016
 * Time: 11:16
 * Created with IntelliJ IDEA.
 */
public class GOTermPredicateTest {

    @Test
    public void isTrueIfCandidateIdHasCorrectFormat(){
        assertThat(isValidGOTermId().test("GO:1234123"),is(true));
    }

    @Test
    public void isFalseIfCandidateIdIsHasIncorrectFormat(){
        assertThat(isValidGOTermId().test("GO:ABCDEFGH"),is(false));
    }
}
