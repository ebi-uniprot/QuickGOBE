package uk.ac.ebi.quickgo.repo.solr.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * Tests the {@link Facet} implementation.
 */
@RunWith(MockitoJUnitRunner.class)
public class FacetTest {
    @Mock
    private QueryVisitor visitorMock;

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
