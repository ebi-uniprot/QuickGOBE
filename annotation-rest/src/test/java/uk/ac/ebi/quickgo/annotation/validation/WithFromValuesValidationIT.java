package uk.ac.ebi.quickgo.annotation.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.validation.MockValidationConfig.ID_FAILS;
import static uk.ac.ebi.quickgo.annotation.validation.MockValidationConfig.ID_SUCCEEDS;

/**
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 11:01
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockValidationConfig.class, loader = SpringApplicationContextLoader.class)
public class WithFromValuesValidationIT {

    @Autowired
    WithFromValuesValidation validator;

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
