package uk.ac.ebi.quickgo.rest.search.query;

import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 25/08/16
 * @author Edd
 */
class AllQueryTest {
    @Test
    void equalsFindsEqualObjectsEqual() {
        AllQuery allQuery1 = new AllQuery();
        AllQuery allQuery2 = new AllQuery();

        assertThat(allQuery1, is(allQuery2));
    }

    @Test
    void equalsFindsNonEqualObjectsDifferent() {
        AllQuery allQuery1 = new AllQuery();
        QuickGOQuery otherQuery = QuickGOQuery.not(allQuery1);

        assertThat(allQuery1, IsNot.not(otherQuery));
    }

    @Test
    void hashCodeIsSameWhenCalledMultipleTimesOnSameObject() {
        AllQuery allQuery1 = new AllQuery();

        assertThat(allQuery1.hashCode(), is(allQuery1.hashCode()));
    }

    @Test
    void equalObjectsHaveSameHashCode() {
        AllQuery allQuery1 = new AllQuery();
        AllQuery allQuery2 = new AllQuery();

        assertThat(allQuery1, is(allQuery2));
        assertThat(allQuery1.hashCode(), is(allQuery2.hashCode()));
    }

    /**
     * Generally, this test is not part of the hashcode contract:
     * non-equal objects can have the same hashcode. This test is
     * a sanity check.
     */
    @Test
    void allQueryAndNotAllQueryObjectsAreDifferentAndHaveDifferentHashCodes() {
        AllQuery allQuery1 = new AllQuery();
        QuickGOQuery otherQuery = QuickGOQuery.not(allQuery1);

        assertThat(allQuery1, IsNot.not(otherQuery));
        assertThat(allQuery1.hashCode(), IsNot.not(otherQuery.hashCode()));
    }

}