package uk.ac.ebi.quickgo.geneproduct.common.common;

import uk.ac.ebi.quickgo.geneproduct.common.Proteome;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.geneproduct.common.Proteome.*;

public class ProteomeTest {

    @Test
    public void fromString() {

        assertThat(Proteome.fromString(null), is(NOT_APPLICABLE));

        assertThat(Proteome.fromString("gcrpCan"), is(REFERENCE));
        assertThat(Proteome.fromString("GcrpCan"), is(REFERENCE));

        assertThat(Proteome.fromString("Complete"), is(COMPLETE));
        assertThat(Proteome.fromString("CompLete"), is(COMPLETE));

        assertThat(Proteome.fromString("None"), is(NONE));
        assertThat(Proteome.fromString("NoNe"), is(NONE));

        assertThat(Proteome.fromString("gcrpIso"), is(IS_ISOFORM));
        assertThat(Proteome.fromString("GcrpIso"), is(IS_ISOFORM));

    }
}
