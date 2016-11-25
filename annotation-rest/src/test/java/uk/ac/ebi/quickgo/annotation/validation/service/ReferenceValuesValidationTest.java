package uk.ac.ebi.quickgo.annotation.validation.service;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for ReferenceValuesValidation.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 09:38
 * Created with IntelliJ IDEA.
 */
public class ReferenceValuesValidationTest {

    private static final String ID_SUCCEEDS = "PMID:123456";
    private static final String ID_FAILS = "PMID:ZZZZZZZZ";

    private ReferenceValuesValidation refValidator;

    @Before
    public void setup() {
        ValidationEntityChecker validationEntityChecker = mock(ValidationEntityChecker.class);
        refValidator = new ReferenceValuesValidation(validationEntityChecker);
        when(validationEntityChecker.isValid(ID_SUCCEEDS)).thenReturn(true);
        when(validationEntityChecker.isValid(ID_FAILS)).thenReturn(false);
    }

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
