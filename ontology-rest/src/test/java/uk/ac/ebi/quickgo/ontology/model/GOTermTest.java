package uk.ac.ebi.quickgo.ontology.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test behaviour of {@link GOTerm}.
 *
 * Created 07/01/16
 * @author Edd
 */
class GOTermTest {

    @Test
    void stringToUsageFindsSuccessfully() {
        GOTerm.Usage usage = GOTerm.Usage.fromFullName("Unrestricted");
        assertThat(usage, is(not(nullValue())));

        usage = GOTerm.Usage.fromFullName("Electronic");
        assertThat(usage, is(not(nullValue())));

        usage = GOTerm.Usage.fromFullName("None");
        assertThat(usage, is(not(nullValue())));
    }

    @Test
    void stringToUsageProducesIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> GOTerm.Usage.fromFullName("SAUSAGES"));
    }
}
