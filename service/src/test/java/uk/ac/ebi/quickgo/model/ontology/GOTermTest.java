package uk.ac.ebi.quickgo.model.ontology;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * Test behaviour of {@link GOTerm}.
 * Created 07/01/16
 * @author Edd
 */
public class GOTermTest {
    @Test
    public void stringToAspectFindsSuccessfully() {
        GOTerm.Aspect aspect = GOTerm.Aspect.string2Aspect("P");
        assertThat(aspect, is(not(nullValue())));

        aspect = GOTerm.Aspect.string2Aspect("F");
        assertThat(aspect, is(not(nullValue())));

        aspect = GOTerm.Aspect.string2Aspect("C");
        assertThat(aspect, is(not(nullValue())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToAspectProducesIllegalArgumentException() {
        GOTerm.Aspect.string2Aspect("SAUSAGES");
    }
}