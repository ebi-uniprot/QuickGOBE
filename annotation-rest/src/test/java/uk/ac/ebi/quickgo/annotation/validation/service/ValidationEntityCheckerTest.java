package uk.ac.ebi.quickgo.annotation.validation.service;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntities;
import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
        ValidationEntity validationEntity = mock(ValidationEntity.class);
        List<ValidationEntity> entities = Collections.singletonList(validationEntity);
        ValidationEntities validationEntities = mock(ValidationEntities.class);

        checker = new ValidationEntityChecker(validationEntities);
        when(validationEntities.get("interpro")).thenReturn(entities);
        when(validationEntity.test("IPR123456")).thenReturn(true);
        when(validationEntity.test("ZZZ123456")).thenReturn(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsConstructionIfPassedInValidatorIsNull(){
        new ValidationEntityChecker(null);
    }

    @Test
    public void verificationPasses() {
        assertThat(checker.isValid("InterPro:IPR123456"), is(true));
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
