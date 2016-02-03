package uk.ac.ebi.quickgo.common.search.results;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Tests the implementation of the {@link Facet} implementation.
 */
public class FacetTest {
    @Test
    public void newlyCreateFacetHasNoFacets() throws Exception {
        Facet facet = new Facet();

        assertThat(facet.getFacetFields(), is(empty()));
    }

    @Test
    public void addFacetField() throws Exception {
        FieldFacet fieldFacet1 = new FieldFacet("field1");
        FieldFacet fieldFacet2 = new FieldFacet("field2");
        FieldFacet fieldFacet3 = new FieldFacet("field3");

        Facet facet = new Facet();
        facet.addFacetField(fieldFacet1);
        facet.addFacetField(fieldFacet2);
        facet.addFacetField(fieldFacet3);

        assertThat(facet.getFacetFields(), hasSize(3));
        assertThat(facet.getFacetFields(), containsInAnyOrder(fieldFacet1, fieldFacet2, fieldFacet3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void deletingFacetFieldThrowsException() throws Exception {
        FieldFacet fieldFacet1 = new FieldFacet("field1");

        Facet facet = new Facet();
        facet.addFacetField(fieldFacet1);

        facet.getFacetFields().remove(0);
    }
}
