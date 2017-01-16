package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link SortCriterion}.
 *
 * Created 16/01/17
 * @author Edd
 */
public class SortCriterionTest {
    @Test
    public void canCreateSortCriterion() {
        String field = "fieldValue";
        SortCriterion.SortOrder order = SortCriterion.SortOrder.ASC;
        SortCriterion criterion = new SortCriterion(field, order);
        assertThat(criterion.getSortField().getField(), is(field));
        assertThat(criterion.getSortOrder(), is(order));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sortCriterionWithNullOrderCausesException() {
        new SortCriterion("value", null);
    }
}