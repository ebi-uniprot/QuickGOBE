package uk.ac.ebi.quickgo.annotation.validation;

import javax.validation.ConstraintValidatorContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 09:38
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockValidationConfig.class, loader = SpringApplicationContextLoader.class)
@ActiveProfiles(profiles = MockValidationConfig.DB_REF_VALIDATION_SUCCEEDS)
public class ReferenceValuesValidationIT {

    @Autowired
    ReferenceValuesValidation refValidator;
    private ConstraintValidatorContext mockContext;

    @Before
    public void setup(){
        mockContext = mock(ConstraintValidatorContext.class);
    }

    @Test
    public void validationSucceedsIfKnownDb(){
        assertThat(refValidator.isValid(new String[]{"PMID:123456"}, mockContext), is(true));
    }

    @Test
    public void validationSucceedsIfDbNotSpecified(){
        assertThat(refValidator.isValid(new String[]{"123456"}, mockContext), is(true));
    }

    @Test
    public void validationSucceedsIfArgumentListIsNull(){
        assertThat(refValidator.isValid(null, mockContext), is(true));
    }

    @Test
    public void validationFailsIfUnknownDb(){
        assertThat(refValidator.isValid(new String[]{"XXXX:123456"}, mockContext), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validationFailsIfArgumentListContainsNull(){
       refValidator.isValid(new String[]{null}, mockContext);
    }
}
