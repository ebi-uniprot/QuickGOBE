package uk.ac.ebi.quickgo.annotation.validation;

import javax.validation.ConstraintValidatorContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
/**
 * @author Tony Wardell
 * Date: 10/11/2016
 * Time: 16:23
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReferenceDBXRefEntityValidationTest {

    ReferenceDBXRefEntityValidation refValidator;

    @Mock
    DBXRefEntityValidation mockValidator;

    @Mock
    ConstraintValidatorContext mockContext;

    @Before
    public void setup(){
        refValidator = new ReferenceDBXRefEntityValidation();
        refValidator.dbxRefEntityValidation = mockValidator;
        when(mockValidator.isValid(any(), any())).thenReturn(true);
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

    @Test
    public void validationFailsIfArgumentListContainsNull(){
        assertThat(refValidator.isValid(new String[]{null}, mockContext), is(false));
    }
}
