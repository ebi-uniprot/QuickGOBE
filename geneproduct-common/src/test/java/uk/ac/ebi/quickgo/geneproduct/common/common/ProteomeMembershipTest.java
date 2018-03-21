package uk.ac.ebi.quickgo.geneproduct.common.common;

import uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.COMPLETE;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.NONE;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.NOTAPPLICABLE;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.REFERENCE;

/**
 * @author Tony Wardell
 * Date: 15/03/2018
 * Time: 13:16
 * Created with IntelliJ IDEA.
 */
public class ProteomeMembershipTest {

    @Test
    public void checkMembershipCreationRules() {
        assertThat(ProteomeMembership.membership(() -> false, () -> true, () -> true), is(NOTAPPLICABLE));
        assertThat(ProteomeMembership.membership(() -> false, () -> false, () -> true), is(NOTAPPLICABLE));
        assertThat(ProteomeMembership.membership(() -> false, () -> true, () -> false), is(NOTAPPLICABLE));
        assertThat(ProteomeMembership.membership(() -> true, () -> true, () -> true), is(REFERENCE));
        assertThat(ProteomeMembership.membership(() -> true, () -> true, () -> false), is(REFERENCE));
        assertThat(ProteomeMembership.membership(() -> true, () -> false, () -> true), is(COMPLETE));
        assertThat(ProteomeMembership.membership(() -> true, () -> false, () -> false), is(NONE));
    }

}
