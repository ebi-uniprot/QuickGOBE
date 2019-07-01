package uk.ac.ebi.quickgo.annotation.validation.service;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

/**
 * Test the
 * @author Tony Wardell
 * Date: 09/11/2016
 * Time: 17:27
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidationEntityCheckerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ValidationEntityChecker checker;

    @Before
    public void setup() throws Exception {
        ValidationEntity validationEntity1 = mock(ValidationEntity.class);
        ValidationEntity validationEntity2 = mock(ValidationEntity.class);
        ValidationEntity validationEntity3 = mock(ValidationEntity.class);

        checker = new ValidationEntityChecker();

        when(validationEntity1.keyValue()).thenReturn("interpro");
        when(validationEntity2.keyValue()).thenReturn("intact");
        when(validationEntity3.keyValue()).thenReturn("uniprotkb");
        when(validationEntity2.test("EBI-11166735")).thenReturn(true);
        checker.addEntities(Arrays.asList(validationEntity1, validationEntity2, validationEntity3));

    }

    @Test
    public void verificationPasses() {
        assertThat(checker.isValid("IntAct:EBI-11166735"), is(true));
    }

    @Test
    public void idWithDatabasePassesVerification() {
        assertThat(checker.isValid("Zebra:ZZZ123456"), is(false));
    }

    @Test
    public void idWithoutDatabasePassesVerification() {
        assertThat(checker.isValid("xxx"), is(true));
    }

    @Test
    public void idWithKnownDatabaseButNoIdPartFailsVerification() {
        assertThat(checker.isValid("InterPro:"), is(false));
    }

    @Test
    public void idWithUnknownDatabaseButNoIdPartFailsVerification() {
        assertThat(checker.isValid("Dell:"), is(false));
    }

    @Test
    public void idWithUnknownDatabaseAndIdPartFailsVerification() {
        assertThat(checker.isValid("Dell:12345"), is(false));
    }

    @Test
    public void idContainsOnlyColonIsNotVerified() {
        assertThat(checker.isValid(":"), is(false));
    }

    @Test
    public void nullIdFailsVerification() {
        assertThat(checker.isValid(null), is(false));
    }
}
