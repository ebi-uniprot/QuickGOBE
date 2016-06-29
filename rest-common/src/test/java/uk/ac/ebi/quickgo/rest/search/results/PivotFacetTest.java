package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.quickgo.rest.search.results.PivotFacet.*;

/**
 * Tests the behaviour of the {@link PivotFacet} class.
 */
public class PivotFacetTest {
    private String[] pivotFields = {"field1", "field2"};
    private String field = "field";
    private String value = "value";
    private Long count = 1L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullFieldsInPivotFacetCreationThrowsException() throws Exception {
        String[] fields = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pivot fields cannot be null or empty");

        new PivotFacet(fields);
    }

    @Test
    public void emptyFieldsInPivotFacetCreationThrowsException() throws Exception {
        String[] pivotFields = {};

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Pivot fields cannot be null or empty");

        new PivotFacet(pivotFields);
    }

    @Test
    public void retrievingFieldsInPivotFacetIsSuccessful() throws Exception {
        String[] pivotFields = {"field1", "field2", "field3"};

        PivotFacet pivotFacet = new PivotFacet(pivotFields);

        assertThat(pivotFacet.getFields(), arrayWithSize(3));
        assertThat(pivotFacet.getFields(), arrayContaining(pivotFields));
    }

    @Test
    public void addingNullPivotToPivotFacetThrowsException() throws Exception {
        Pivot pivot = null;

        PivotFacet pivotFacet = new PivotFacet(pivotFields);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null pivot to facet");

        pivotFacet.addPivot(pivot);
    }

    @Test
    public void addingMultiplePopulatedPivotsToPivotFacetIsSuccessful() throws Exception {
        Pivot pivot1 = new Pivot(field + "1", value, count);
        Pivot pivot2 = new Pivot(field + "2", value, count);
        Pivot pivot3 = new Pivot(field + "3", value, count);

        PivotFacet pivotFacet = new PivotFacet(pivotFields);

        pivotFacet.addPivot(pivot1);
        pivotFacet.addPivot(pivot2);
        pivotFacet.addPivot(pivot3);

        assertThat(pivotFacet.getPivots(), hasSize(3));
        assertThat(pivotFacet.getPivots(), contains(pivot1, pivot2, pivot3));
    }

    @Test
    public void nullFieldNameInPivotCreationThrowsException() throws Exception {
        String field = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field cannot be null or empty");

        new Pivot(field, value, count);
    }

    @Test
    public void emptyFieldNameInPivotCreationThrowsException() throws Exception {
        String field = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field cannot be null or empty");

        new Pivot(field, value, count);
    }

    @Test
    public void nullValueInPivotCreationThrowsException() throws Exception {
        String value = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Value cannot be null or empty");

        new Pivot(field, value, count);
    }

    @Test
    public void emptyValueInPivotCreationThrowsException() throws Exception {
        String value = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Value cannot be null or empty");

        new Pivot(field, value, count);
    }

    @Test
    public void negativeCountNameInPivotCreationThrowsException() throws Exception {
        Long count = -1L;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Count cannot be negative");

        new Pivot(field, value, count);
    }

    @Test
    public void canCreatePivotWithFieldAndValueAndCount() throws Exception {
        Pivot pivotFacet = new Pivot(field, value, count);
        assertThat(pivotFacet, is(notNullValue()));

        assertThat(pivotFacet.getField(), is(field));
        assertThat(pivotFacet.getValue(), is(value));
        assertThat(pivotFacet.getCount(), is(count));
    }

    @Test
    public void nullChildPivotThrowsException() throws Exception {
        Pivot pivot = new Pivot(field, value, count);

        Pivot childPivot = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null child pivot");

        pivot.addPivot(childPivot);
    }

    @Test
    public void canAddMultipleChildrenToParentPivot() throws Exception {
        String parentName = "parent";
        String childName1 = "child1";
        String childName2 = "child2";

        Pivot parentPivot = new Pivot(parentName, value, count);
        Pivot childPivot1 = new Pivot(childName1, value, count);
        Pivot childPivot2 = new Pivot(childName2, value, count);

        parentPivot.addPivot(childPivot1);
        parentPivot.addPivot(childPivot2);

        assertThat(parentPivot.getPivots(), hasSize(2));
        assertThat(parentPivot.getPivots(), contains(childPivot1, childPivot2));
    }

    @Test
    public void canAddChildToParentWithGrandParentPivot() throws Exception {
        String grandparentName = "grandparent";
        String parentName = "parent";
        String childName = "child";

        Pivot grandParentPivot = new Pivot(grandparentName, value, count);
        Pivot parentPivot = new Pivot(parentName, value, count);
        Pivot childPivot = new Pivot(childName, value, count);

        grandParentPivot.addPivot(parentPivot);
        parentPivot.addPivot(childPivot);

        assertThat(grandParentPivot.getPivots(), hasSize(1));
        Pivot retrievedParent = grandParentPivot.getPivots().iterator().next();
        assertThat(retrievedParent.getField(), is(parentName));

        assertThat(parentPivot.getPivots(), hasSize(1));
        Pivot retrievedChild = parentPivot.getPivots().iterator().next();
        assertThat(retrievedChild.getField(), is(childName));
    }
}