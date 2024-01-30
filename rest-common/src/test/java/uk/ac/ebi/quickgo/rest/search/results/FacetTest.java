package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the implementation of the {@link Facet} implementation.
 */
class FacetTest {

    private Facet facet;

    @BeforeEach
    void setUp() {
        facet = new Facet();
    }

    @Test
    void newlyCreateFacetHasNoFacets() {
        assertThat(facet.getFacetFields(), is(empty()));
    }

    @Test
    void addingNullFacetFieldThrowsException() {
        FieldFacet fieldFacet = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> facet.addFacetField(fieldFacet));
        assertTrue(exception.getMessage().contains("Cannot add null field facet."));
    }

    @Test
    void addFacetField() {
        FieldFacet fieldFacet1 = new FieldFacet("field1");
        FieldFacet fieldFacet2 = new FieldFacet("field2");
        FieldFacet fieldFacet3 = new FieldFacet("field3");

        facet.addFacetField(fieldFacet1);
        facet.addFacetField(fieldFacet2);
        facet.addFacetField(fieldFacet3);

        assertThat(facet.getFacetFields(), hasSize(3));
        assertThat(facet.getFacetFields(), containsInAnyOrder(fieldFacet1, fieldFacet2, fieldFacet3));
    }

    @Test
    void deletingFacetFieldThrowsException() {
        FieldFacet fieldFacet1 = new FieldFacet("field1");

        facet.addFacetField(fieldFacet1);
        assertThrows(UnsupportedOperationException.class, () -> facet.getFacetFields().remove(0));
    }

    @Test
    void addingNullPivotThrowsException() {
        PivotFacet pivotFacet = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> facet.addPivotFacet(pivotFacet));
        assertTrue(exception.getMessage().contains("Cannot add null pivot facet."));
    }

    @Test
    void addingNonNullPivotFacetIsSuccessful() {
        String[] pivotFields = {"field1", "field2"};
        PivotFacet pivotFacet = new PivotFacet(pivotFields);

        facet.addPivotFacet(pivotFacet);

        assertThat(facet.getPivotFacets(), hasSize(1));
        assertThat(facet.getPivotFacets(), contains(pivotFacet));
    }
}