package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.results.Facet;
import uk.ac.ebi.quickgo.rest.search.results.FieldFacet;

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
        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet1 = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet("field1");
        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet2 = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet("field2");
        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet3 = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet("field3");

        Facet facet = new Facet();
        facet.addFacetField(fieldFacet1);
        facet.addFacetField(fieldFacet2);
        facet.addFacetField(fieldFacet3);

        assertThat(facet.getFacetFields(), hasSize(3));
        assertThat(facet.getFacetFields(), containsInAnyOrder(fieldFacet1, fieldFacet2, fieldFacet3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void deletingFacetFieldThrowsException() throws Exception {
        uk.ac.ebi.quickgo.rest.search.results.FieldFacet fieldFacet1 = new FieldFacet("field1");

        Facet facet = new Facet();
        facet.addFacetField(fieldFacet1);

        facet.getFacetFields().remove(0);
    }
}
