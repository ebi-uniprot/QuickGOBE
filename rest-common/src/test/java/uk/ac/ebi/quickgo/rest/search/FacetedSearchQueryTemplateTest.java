package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.Facet;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Tests the behaviour of the {@link FacetedSearchQueryTemplate} class.
 */
public class FacetedSearchQueryTemplateTest {
    private FacetedSearchQueryTemplate searchTemplate;

    @Before
    public void setUp() throws Exception {
        searchTemplate = new FacetedSearchQueryTemplate();
    }

    @Test
    public void newBuilderInheritsDefinitionsFromFakeBuilder() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field","value");
        FacetedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder(query));

        QueryRequest request = builder.build();

        assertThat(request.getQuery(), is(query));
    }


    @Test
    public void newBuilderCreatesQueryRequestWithNoFacets() throws Exception {
        FacetedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());

        QueryRequest request = builder.build();

        assertThat(request.getFacets(), hasSize(0));
    }

    @Test
    public void addedFacetToBuilderCreatesQueryRequestWith1Facet() throws Exception {
        String facetField = "field";
        FacetedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());
        builder.addFacets(facetField);

        QueryRequest request = builder.build();

        Collection<Facet> actualFacets = request.getFacets();

        Facet expectedFacet = new Facet(facetField);

        assertThat(actualFacets, contains(expectedFacet));
    }

    @Test
    public void twoAddedFacetsToBuilderCreatesQueryRequestWith2Facets() throws Exception {
        String facetField1 = "field1";
        String facetField2 = "field2";

        FacetedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());
        builder.addFacets(facetField1, facetField2);

        QueryRequest request = builder.build();

        Collection<Facet> actualFacets = request.getFacets();

        Facet expectedFacet1 = new Facet(facetField1);
        Facet expectedFacet2 = new Facet(facetField2);

        assertThat(actualFacets, containsInAnyOrder(expectedFacet1, expectedFacet2));
    }

}