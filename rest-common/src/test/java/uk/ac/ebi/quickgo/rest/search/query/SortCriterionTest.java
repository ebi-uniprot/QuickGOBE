package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link SortCriterion}.
 *
 * Created 16/01/17
 * @author Edd
 */
class SortCriterionTest {
    @Test
    void canCreateSortCriterion() {
        String field = "fieldValue";
        SortCriterion.SortOrder order = SortCriterion.SortOrder.ASC;
        SortCriterion criterion = new SortCriterion(field, order);
        assertThat(criterion.getSortField().getField(), is(field));
        assertThat(criterion.getSortOrder(), is(order));
    }

    @Test
    void sortCriterionWithNullOrderCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new SortCriterion("value", null));
    }
}