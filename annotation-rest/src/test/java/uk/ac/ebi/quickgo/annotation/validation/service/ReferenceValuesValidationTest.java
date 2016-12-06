package uk.ac.ebi.quickgo.annotation.validation.service;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationProperties;

import java.util.Arrays;
import java.util.List;
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

    private static final String ID_SUCCEEDS_1 = "PMID:123456";
    private static final String ID_SUCCEEDS_2 = "PMID:223456";
    private static final String ID_SUCCEEDS_3 = "PMID:323456";
    private static final String ID_FAILS_1 = "PMID:ZZZZZZZX";
    private static final String ID_FAILS_2 = "PMID:ZZZZZZZY";
    private static final String ID_FAILS_3 = "PMID:ZZZZZZZZ";
    private final List<String> referenceDatabases = Arrays.asList("pmid","doi","go_ref","reactome");
    private ReferenceValuesValidation refValidator;

    @Before
    public void setup() {
        ValidationEntityChecker validationEntityChecker = mock(ValidationEntityChecker.class);
        ValidationProperties validationProperties = mock(ValidationProperties.class);
        refValidator = new ReferenceValuesValidation(validationEntityChecker, validationProperties);
        when(validationEntityChecker.isValid(ID_SUCCEEDS_1)).thenReturn(true);
        when(validationEntityChecker.isValid(ID_SUCCEEDS_2)).thenReturn(true);
        when(validationEntityChecker.isValid(ID_SUCCEEDS_3)).thenReturn(true);
        when(validationEntityChecker.isValid(ID_FAILS_1)).thenReturn(false);
        when(validationEntityChecker.isValid(ID_FAILS_2)).thenReturn(false);
        when(validationEntityChecker.isValid(ID_FAILS_3)).thenReturn(false);
        when(validationProperties.getReferenceDbs()).thenReturn(referenceDatabases);
    }

    @Test
    public void validationSucceedsIfKnownDb(){
        assertThat(refValidator.isValid(new String[]{ID_SUCCEEDS_1}, null), is(true));
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
    public void validationFailsIfDbKnownButIdIsIncorrect(){
        assertThat(refValidator.isValid(new String[]{ID_FAILS_1}, null), is(false));
    }

    @Test
    public void validationFailsIfUnknownDb(){
        assertThat(refValidator.isValid(new String[]{"XXXX:123456"}, null), is(false));
    }

    @Test
    public void validationFailsIfArgumentListContainsNull(){
        assertThat(refValidator.isValid(new String[]{null}, null), is(false));
    }

    @Test
    public void validationSucceedsForMultipleValidValues(){
        assertThat(refValidator.isValid(new String[]{ID_SUCCEEDS_1, ID_SUCCEEDS_2, ID_SUCCEEDS_3}, null), is(true));
    }

    @Test
    public void validationFailsForMultipleInvalidValues(){
        assertThat(refValidator.isValid(new String[]{ID_FAILS_1, ID_FAILS_2, ID_FAILS_3}, null), is(false));
    }

    @Test
    public void validationFailsForMixtureOfValidAndInvalidValues(){
        assertThat(refValidator.isValid(new String[]{ID_SUCCEEDS_1, ID_FAILS_2, ID_SUCCEEDS_3}, null), is(false));
    }
}
