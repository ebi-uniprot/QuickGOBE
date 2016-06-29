package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests the behaviour of the {@link PivotFacet} class.
 */
public class PivotFacetTest {
    private String name = "name";
    private String catName = "catName";
    private Long count = 1L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullFieldNameThrowsException() throws Exception {
        String field = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field cannot be null or empty");

        new PivotFacet(field, catName, count);
    }

    @Test
    public void emptyFieldNameThrowsException() throws Exception {
        String field = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field cannot be null or empty");

        new PivotFacet(field, catName, count);
    }

    @Test
    public void nullValueThrowsException() throws Exception {
        String value = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Value cannot be null or empty");

        new PivotFacet(name, value, count);
    }

    @Test
    public void emptyValueThrowsException() throws Exception {
        String value = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Value cannot be null or empty");

        new PivotFacet(name, value, count);
    }

    @Test
    public void negativeCountNameThrowsException() throws Exception {
        Long count = -1L;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Count cannot be negative");

        new PivotFacet(name, catName, count);
    }

    @Test
    public void canCreateWithFieldAndValueAndCount() throws Exception {
        PivotFacet pivotFacet = new PivotFacet(name, catName, count);
        assertThat(pivotFacet, is(notNullValue()));

        assertThat(pivotFacet.getField(), is(name));
        assertThat(pivotFacet.getValue(), is(catName));
        assertThat(pivotFacet.getCount(), is(count));
    }

    @Test
    public void nullChildPivotThrowsException() throws Exception {
        PivotFacet pivotFacet = new PivotFacet(name, catName, count);

        PivotFacet childPivot = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null child pivot");

        pivotFacet.addPivot(childPivot);
    }

    @Test
    public void canAddMultipleChildrenToParent() throws Exception {
        String parentName = "parent";
        String childName1 = "child1";
        String childName2 = "child2";

        PivotFacet parentPivot = new PivotFacet(parentName, catName, count);
        PivotFacet childPivot1 = new PivotFacet(childName1, catName, count);
        PivotFacet childPivot2 = new PivotFacet(childName2, catName, count);

        parentPivot.addPivot(childPivot1);
        parentPivot.addPivot(childPivot2);

        assertThat(parentPivot.getPivots(), hasSize(2));
        assertThat(parentPivot.getPivots(), containsInAnyOrder(childPivot1, childPivot2));
    }

    @Test
    public void canAddChildToParentWithGrandParent() throws Exception {
        String grandparentName = "grandparent";
        String parentName = "parent";
        String childName = "child";

        PivotFacet grandParentPivot = new PivotFacet(grandparentName, catName, count);
        PivotFacet parentPivot = new PivotFacet(parentName, catName, count);
        PivotFacet childPivot = new PivotFacet(childName, catName, count);

        grandParentPivot.addPivot(parentPivot);
        parentPivot.addPivot(childPivot);

        assertThat(grandParentPivot.getPivots(), hasSize(1));
        PivotFacet retrievedParent = grandParentPivot.getPivots().iterator().next();
        assertThat(retrievedParent.getField(), is(parentName));

        assertThat(parentPivot.getPivots(), hasSize(1));
        PivotFacet retrievedChild = parentPivot.getPivots().iterator().next();
        assertThat(retrievedChild.getField(), is(childName));
    }
}