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
 * Tests the behaviour of the {@link FacetPivot} class.
 */
public class FacetPivotTest {
    private String name = "name";
    private String catName = "catName";
    private Long count = 1L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullNameThrowsException() throws Exception {
        String name = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new FacetPivot(name, catName, count);
    }

    @Test
    public void emptyNameThrowsException() throws Exception {
        String name = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Name cannot be null or empty");

        new FacetPivot(name, catName, count);
    }

    @Test
    public void nullCategoryNameThrowsException() throws Exception {
        String catName = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Category name cannot be null or empty");

        new FacetPivot(name, catName, count);
    }

    @Test
    public void emptyCategoryNameThrowsException() throws Exception {
        String catName = "";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Category name cannot be null or empty");

        new FacetPivot(name, catName, count);
    }

    @Test
    public void negativeCountNameThrowsException() throws Exception {
        Long count = -1L;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Count cannot be negative");

        new FacetPivot(name, catName, count);
    }

    @Test
    public void canCreateWithNameAndCategoryNameAndCount() throws Exception {
        FacetPivot facetPivot = new FacetPivot(name, catName, count);
        assertThat(facetPivot, is(notNullValue()));

        assertThat(facetPivot.getName(), is(name));
        assertThat(facetPivot.getCatName(), is(catName));
        assertThat(facetPivot.getCount(), is(count));
    }

    @Test
    public void nullChildPivotThrowsException() throws Exception {
        FacetPivot facetPivot = new FacetPivot(name, catName, count);

        FacetPivot childPivot = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null child pivot");

        facetPivot.addPivot(childPivot);
    }

    @Test
    public void canAddMultipleChildrenToParent() throws Exception {
        String parentName = "parent";
        String childName1 = "child1";
        String childName2 = "child2";

        FacetPivot parentPivot = new FacetPivot(parentName, catName, count);
        FacetPivot childPivot1 = new FacetPivot(childName1, catName, count);
        FacetPivot childPivot2 = new FacetPivot(childName2, catName, count);

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

        FacetPivot grandParentPivot = new FacetPivot(grandparentName, catName, count);
        FacetPivot parentPivot = new FacetPivot(parentName, catName, count);
        FacetPivot childPivot = new FacetPivot(childName, catName, count);

        grandParentPivot.addPivot(parentPivot);
        parentPivot.addPivot(childPivot);

        assertThat(grandParentPivot.getPivots(), hasSize(1));
        FacetPivot retrievedParent = grandParentPivot.getPivots().iterator().next();
        assertThat(retrievedParent.getName(), is(parentName));

        assertThat(parentPivot.getPivots(), hasSize(1));
        FacetPivot retrievedChild = parentPivot.getPivots().iterator().next();
        assertThat(retrievedChild.getName(), is(childName));
    }
}