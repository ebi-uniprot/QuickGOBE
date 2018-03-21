package uk.ac.ebi.quickgo.geneproduct.common.common;

import uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.COMPLETE;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.NONE;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.NOT_APPLICABLE;
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
        assertThat(ProteomeMembership.membership(() -> false, () -> true, () -> true), is(NOT_APPLICABLE.name()));
        assertThat(ProteomeMembership.membership(() -> false, () -> false, () -> true), is(NOT_APPLICABLE.name()));
        assertThat(ProteomeMembership.membership(() -> false, () -> true, () -> false), is(NOT_APPLICABLE.name()));
        assertThat(ProteomeMembership.membership(() -> true, () -> true, () -> true), is(REFERENCE.name()));
        assertThat(ProteomeMembership.membership(() -> true, () -> true, () -> false), is(REFERENCE.name()));
        assertThat(ProteomeMembership.membership(() -> true, () -> false, () -> true), is(COMPLETE.name()));
        assertThat(ProteomeMembership.membership(() -> true, () -> false, () -> false), is(NONE.name()));
    }

}
