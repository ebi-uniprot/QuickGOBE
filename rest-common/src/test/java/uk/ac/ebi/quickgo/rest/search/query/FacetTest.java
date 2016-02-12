package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Tests the {@link Facet} implementation.
 */
public class FacetTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        new Facet(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFieldThrowsException() throws Exception {
        new Facet("");
    }

    @Test
    public void createFacet() throws Exception {
        String facetField = "field";
        Facet facet = new Facet(facetField);

        assertThat(facet.getField(), is(equalTo(facetField)));
    }
}
