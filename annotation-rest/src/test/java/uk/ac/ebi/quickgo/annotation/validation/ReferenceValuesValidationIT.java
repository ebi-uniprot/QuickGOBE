package uk.ac.ebi.quickgo.annotation.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.annotation.validation.MockValidationConfig.ID_SUCCEEDS;

/**
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 09:38
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockValidationConfig.class)
public class ReferenceValuesValidationIT {

    @Autowired
    ReferenceValuesValidation refValidator;

    @Test
    public void validationSucceedsIfKnownDb(){
        assertThat(refValidator.isValid(new String[]{ID_SUCCEEDS}, null), is(true));
    }

    @Test
    public void validationSucceedsIfDbNotSpecified(){
        assertThat(refValidator.isValid(new String[]{"123456"}, null), is(true));
    }

    @Test
    public void validationSucceedsIfArgumentListIsNull(){
        assertThat(refValidator.isValid(null, null), is(true));
    }

    @Test
    public void validationFailsIfUnknownDb(){
        assertThat(refValidator.isValid(new String[]{"XXXX:123456"}, null), is(false));
    }

    @Test
    public void validationFailsIfArgumentListContainsNull(){
        assertThat(refValidator.isValid(new String[]{null}, null), is(false));

    }
}
