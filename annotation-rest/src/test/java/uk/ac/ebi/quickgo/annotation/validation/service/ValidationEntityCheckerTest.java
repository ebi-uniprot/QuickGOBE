package uk.ac.ebi.quickgo.annotation.validation.service;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the
 * @author Tony Wardell
 * Date: 09/11/2016
 * Time: 17:27
 * Created with IntelliJ IDEA.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
class ValidationEntityCheckerTest {
    private ValidationEntityChecker checker;

    @BeforeEach
    void setup() throws Exception {
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
    void verificationPasses() {
        assertThat(checker.isValid("IntAct:EBI-11166735"), is(true));
    }

    @Test
    void idWithDatabasePassesVerification() {
        assertThat(checker.isValid("Zebra:ZZZ123456"), is(false));
    }

    @Test
    void idWithoutDatabasePassesVerification() {
        assertThat(checker.isValid("xxx"), is(true));
    }

    @Test
    void idWithKnownDatabaseButNoIdPartFailsVerification() {
        assertThat(checker.isValid("InterPro:"), is(false));
    }

    @Test
    void idWithUnknownDatabaseButNoIdPartFailsVerification() {
        assertThat(checker.isValid("Dell:"), is(false));
    }

    @Test
    void idWithUnknownDatabaseAndIdPartFailsVerification() {
        assertThat(checker.isValid("Dell:12345"), is(false));
    }

    @Test
    void idContainsOnlyColonIsNotVerified() {
        assertThat(checker.isValid(":"), is(false));
    }

    @Test
    void nullIdFailsVerification() {
        assertThat(checker.isValid(null), is(false));
    }
}
