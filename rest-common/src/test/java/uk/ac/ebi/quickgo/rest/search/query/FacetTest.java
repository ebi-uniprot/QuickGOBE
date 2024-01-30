package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the {@link Facet} implementation.
 */
class FacetTest {

    @Test
    void nullFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Facet(null));
    }

    @Test
    void emptyFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Facet(""));
    }

    @Test
    void createFacet() {
        String facetField = "field";
        Facet facet = new Facet(facetField);

        assertThat(facet.getField(), is(equalTo(facetField)));
    }
}
