package uk.ac.ebi.quickgo.geneproduct.model;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * Tests methods and structure of AnnotationRequest
 *
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 11:25
 * Created with IntelliJ IDEA.
 */
class GeneProductRequestTest {

    private GeneProductRequest geneProductRequest;

    @BeforeEach
    void setUp() {
        geneProductRequest = new GeneProductRequest();
    }

    @Test
    void defaultPageAndLimitValuesAreCorrect() {
        assertThat(geneProductRequest.getPage(), equalTo(1));
        assertThat(geneProductRequest.getLimit(), equalTo(25));
    }

    @Test
    void successfullySetAndGetPageAndLimitValues() {
        geneProductRequest.setPage(4);
        geneProductRequest.setLimit(15);

        assertThat(geneProductRequest.getPage(), equalTo(4));
        assertThat(geneProductRequest.getLimit(), equalTo(15));
    }

    @Test
    void setAndGetType() {
        String type = "protein";
        geneProductRequest.setType(type);

        assertThat(geneProductRequest.getType(), is(type));
    }

    @Test
    void setAndGetTaxonId() {
        String[] taxonIds = {"1", "2", "3"};

        geneProductRequest.setTaxonId(taxonIds);
        assertThat(geneProductRequest.getTaxonId(), arrayContaining(taxonIds));
    }

    @Test
    void setAndGetDbSubset() {
        String dbSubset = "TrEMBL";
        geneProductRequest.setType(dbSubset);

        assertThat(geneProductRequest.getType(), is(dbSubset));
    }

    @Test
    void setAndCreateQuery() {
        String queryStr = "I am a query";
        QuickGOQuery query = QuickGOQuery.createQuery(queryStr);

        geneProductRequest.setQuery(queryStr);

        assertThat(geneProductRequest.createQuery(), is(query));
    }

    @Test
    void emptyGeneProductCreatesNoFilterRequests() {
        List<FilterRequest> filters = geneProductRequest.createFilterRequests();

        assertThat(filters, hasSize(0));
    }

    @Test
    void createsFilterForDbSubset() {
        String dbSubset = "TrEMBL";

        geneProductRequest.setDbSubset(dbSubset);

        List<FilterRequest> filters = geneProductRequest.createFilterRequests();

        assertThat(filters, hasSize(1));

        FilterRequest filterRequest = filters.get(0);

        assertValueInFilter(filterRequest, dbSubset);
    }

    @Test
    void createsFilterForTaxonId() {
        String taxonId = "1";

        geneProductRequest.setTaxonId(taxonId);

        List<FilterRequest> filters = geneProductRequest.createFilterRequests();

        assertThat(filters, hasSize(1));

        FilterRequest filterRequest = filters.get(0);

        assertValueInFilter(filterRequest, taxonId);
    }

    @Test
    void createsFilterForType() {
        String protein = "protein";

        geneProductRequest.setDbSubset(protein);

        List<FilterRequest> filters = geneProductRequest.createFilterRequests();

        assertThat(filters, hasSize(1));

        FilterRequest filterRequest = filters.get(0);

        assertValueInFilter(filterRequest, protein);
    }

    @Test
    void createsFilterForProteome() {
        String proteomeStatus = "complete";

        geneProductRequest.setProteome(proteomeStatus);

        List<FilterRequest> filters = geneProductRequest.createFilterRequests();

        assertThat(filters, hasSize(1));

        FilterRequest filterRequest = filters.get(0);

        assertValueInFilter(filterRequest, proteomeStatus);
    }

    private void assertValueInFilter(FilterRequest filterRequest, String filterValue) {
        assertThat(filterRequest.getValues(), is(not(empty())));

        assertThat(filterRequest.getValues().stream()
                .flatMap(Collection::stream)
                .filter(value -> value.equals(filterValue))
                .findFirst().isPresent(), is(true));
    }
}
