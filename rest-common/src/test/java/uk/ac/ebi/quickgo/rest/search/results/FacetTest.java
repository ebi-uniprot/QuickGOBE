package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Tests the implementation of the {@link Facet} implementation.
 */
public class FacetTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private Facet facet;

    @Before
    public void setUp() throws Exception {
        facet = new Facet();
    }

    @Test
    public void newlyCreateFacetHasNoFacets() throws Exception {
        assertThat(facet.getFacetFields(), is(empty()));
    }

    @Test
    public void addingNullFacetFieldThrowsException() throws Exception {
        FieldFacet fieldFacet = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null field facet.");

        facet.addFacetField(fieldFacet);
    }

    @Test
    public void addFacetField() throws Exception {
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
    public void deletingFacetFieldThrowsException() throws Exception {
        FieldFacet fieldFacet1 = new FieldFacet("field1");

        thrown.expect(UnsupportedOperationException.class);

        facet.addFacetField(fieldFacet1);

        facet.getFacetFields().remove(0);
    }

    @Test
    public void addingNullPivotThrowsException() throws Exception {
        PivotFacet pivotFacet = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot add null pivot facet.");

        facet.addPivotFacet(pivotFacet);
    }

    @Test
    public void addingNonNullPivotFacetIsSuccessful() throws Exception {
        String[] pivotFields = {"field1", "field2"};
        PivotFacet pivotFacet = new PivotFacet(pivotFields);

        facet.addPivotFacet(pivotFacet);

        assertThat(facet.getPivotFacets(), hasSize(1));
        assertThat(facet.getPivotFacets(), contains(pivotFacet));
    }
}