package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.rest.search.results.PivotFacet.*;

/**
 * Tests the behaviour of the {@link PivotFacet} class.
 */
class PivotFacetTest {
    private final String[] pivotFields = {"field1", "field2"};
    private final String field = "field";
    private final String value = "value";
    private final Long count = 1L;

    @Test
    void nullFieldsInPivotFacetCreationThrowsException()  {
        String[] fields = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PivotFacet(fields));
        assertTrue(exception.getMessage().contains("Pivot fields cannot be null or empty"));
    }

    @Test
    void emptyFieldsInPivotFacetCreationThrowsException()  {
        String[] pivotFields = {};
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new PivotFacet(pivotFields));
        assertTrue(exception.getMessage().contains("Pivot fields cannot be null or empty"));
    }

    @Test
    void retrievingFieldsInPivotFacetIsSuccessful()  {
        String[] pivotFields = {"field1", "field2", "field3"};

        PivotFacet pivotFacet = new PivotFacet(pivotFields);

        assertThat(pivotFacet.getFields(), arrayWithSize(3));
        assertThat(pivotFacet.getFields(), arrayContaining(pivotFields));
    }

    @Test
    void addingNullPivotToPivotFacetThrowsException()  {
        Pivot pivot = null;

        PivotFacet pivotFacet = new PivotFacet(pivotFields);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> pivotFacet.addPivot(pivot));
        assertTrue(exception.getMessage().contains("Cannot add null pivot to facet"));
    }

    @Test
    void addingMultiplePopulatedPivotsToPivotFacetIsSuccessful()  {
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
    void nullFieldNameInPivotCreationThrowsException()  {
        String field = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Pivot(field, value, count));
        assertTrue(exception.getMessage().contains("Field cannot be null or empty"));
    }

    @Test
    void emptyFieldNameInPivotCreationThrowsException()  {
        String field = "";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Pivot(field, value, count));
        assertTrue(exception.getMessage().contains("Field cannot be null or empty"));
    }

    @Test
    void nullValueInPivotCreationThrowsException()  {
        String value = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Pivot(field, value, count));
        assertTrue(exception.getMessage().contains("Value cannot be null or empty"));
    }

    @Test
    void emptyValueInPivotCreationThrowsException()  {
        String value = "";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Pivot(field, value, count));
        assertTrue(exception.getMessage().contains("Value cannot be null or empty"));
    }

    @Test
    void negativeCountNameInPivotCreationThrowsException()  {
        Long count = -1L;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Pivot(field, value, count));
        assertTrue(exception.getMessage().contains("Count cannot be negative"));
    }

    @Test
    void canCreatePivotWithFieldAndValueAndCount()  {
        Pivot pivotFacet = new Pivot(field, value, count);
        assertThat(pivotFacet, is(notNullValue()));

        assertThat(pivotFacet.getField(), is(field));
        assertThat(pivotFacet.getValue(), is(value));
        assertThat(pivotFacet.getCount(), is(count));
    }

    @Test
    void nullChildPivotThrowsException()  {
        Pivot pivot = new Pivot(field, value, count);

        Pivot childPivot = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> pivot.addPivot(childPivot));
        assertTrue(exception.getMessage().contains("Cannot add null child pivot"));
    }

    @Test
    void canAddMultipleChildrenToParentPivot()  {
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
    void canAddChildToParentWithGrandParentPivot()  {
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