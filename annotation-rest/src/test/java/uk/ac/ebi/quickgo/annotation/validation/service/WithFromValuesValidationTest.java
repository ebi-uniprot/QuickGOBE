package uk.ac.ebi.quickgo.annotation.validation.service;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 11:01
 * Created with IntelliJ IDEA.
 */
public class WithFromValuesValidationTest {

    private static final String ID_SUCCEEDS = "PMID:123456";
    private static final String ID_FAILS = "PMID:ZZZZZZZZ";

    private WithFromValuesValidation validator;

    @Before
    public void setup() {
        ValidationEntityChecker validationEntityChecker = mock(ValidationEntityChecker.class);
        validator = new WithFromValuesValidation(validationEntityChecker);
        when(validationEntityChecker.isValid(ID_SUCCEEDS)).thenReturn(true);
        when(validationEntityChecker.isValid(ID_FAILS)).thenReturn(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsConstructionIfPassedInValidatorIsNull(){
        new WithFromValuesValidation(null);
    }

    @Test
    public void passesValidation() {
        String[] idList = {ID_SUCCEEDS};
        assertThat(validator.isValid(idList, null), is(true));
    }

    @Test
    public void failsValidation() {
        String[] idList = {ID_FAILS};
        assertThat(validator.isValid(idList, null), is(false));
    }

    @Test
    public void aMixOfSuccessfulAndFailingIdsResultsInThemAllFailing() {
        String[] idList = {ID_SUCCEEDS, ID_FAILS};
        assertThat(validator.isValid(idList, null), is(false));
    }

    @Test
    public void aMixOfSuccessfulAndNullResultsInThemAllFailing() {
        String[] idList = {ID_SUCCEEDS, null};
        assertThat(validator.isValid(idList, null), is(false));
    }

    @Test
    public void aListContainingOnlyNullResultsInFailure() {
        String[] idList = {null};
        assertThat(validator.isValid(idList, null), is(false));
    }

    @Test
    public void nullListPassesSuccessfully() {
        assertThat(validator.isValid(null, null), is(true));
    }
}
