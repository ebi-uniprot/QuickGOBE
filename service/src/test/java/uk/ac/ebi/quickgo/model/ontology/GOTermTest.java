package uk.ac.ebi.quickgo.model.ontology;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * Test behaviour of {@link GOTerm}.
 *
 * Created 07/01/16
 * @author Edd
 */
public class GOTermTest {
    @Test
    public void stringToAspectFindsSuccessfully() {
        GOTerm.Aspect aspect = GOTerm.Aspect.fromShortName("Process");
        assertThat(aspect, is(not(nullValue())));

        aspect = GOTerm.Aspect.fromShortName("Function");
        assertThat(aspect, is(not(nullValue())));

        aspect = GOTerm.Aspect.fromShortName("Component");
        assertThat(aspect, is(not(nullValue())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToAspectProducesIllegalArgumentException() {
        GOTerm.Aspect.fromShortName("SAUSAGES");
    }

    @Test
    public void stringToUsageFindsSuccessfully() {
        GOTerm.Usage usage = GOTerm.Usage.fromFullName("Unrestricted");
        assertThat(usage, is(not(nullValue())));

        usage = GOTerm.Usage.fromFullName("Electronic");
        assertThat(usage, is(not(nullValue())));

        usage = GOTerm.Usage.fromFullName("None");
        assertThat(usage, is(not(nullValue())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToUsageProducesIllegalArgumentException() {
        GOTerm.Usage.fromFullName("SAUSAGES");
    }
}