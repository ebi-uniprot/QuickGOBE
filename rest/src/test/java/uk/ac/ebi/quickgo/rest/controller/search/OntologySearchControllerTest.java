package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;
import uk.ac.ebi.quickgo.service.search.SearchService;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the {@link SearchController}. Primarily tests
 * user request validation logic. Functional / integration tests are covered by
 * {@link OntologySearchIT} and {@link OntologyUserQueryScoringIT}.
 *
 * Created 25/01/16
 * @author Edd
 */
public class OntologySearchControllerTest {
    private SearchController searchController;
    private OntologyFieldSpec ontologyFieldSpec;

    @Before
    public void setUp() {
        this.ontologyFieldSpec = new OntologyFieldSpec();
        this.searchController = new SearchController(mock(FakeSearchService.class), ontologyFieldSpec);
    }

    // validate query ----------------------------------------------
    @Test
    public void determinesThatQueryIsValidForProcessing() {
        assertThat(searchController.isValidQuery("query"), is(true));
    }

    @Test
    public void determinesThatNullQueryIsInvalidForProcessing() {
        assertThat(searchController.isValidQuery(null), is(false));
    }

    @Test
    public void determinesThatEmptyQueryIsInvalidForProcessing() {
        assertThat(searchController.isValidQuery(""), is(false));
    }

    // validate row num in request ----------------------------------------------
    @Test
    public void determinesThat1IsAValidRowNumsRequest() {
        assertThat(searchController.isValidNumRows(1), is(true));
    }

    @Test
    public void determinesThat0IsAnInvalidRowNumsRequest() {
        assertThat(searchController.isValidNumRows(0), is(false));
    }

    @Test
    public void determinesThatMinus1IsAnInvalidRowNumsRequest() {
        assertThat(searchController.isValidNumRows(-1), is(false));
    }

    // validate page num in request ----------------------------------------------
    @Test
    public void determinesThat1IsAValidPageNumRequest() {
        assertThat(searchController.isValidPage(1), is(true));
    }

    @Test
    public void determinesThat0IsAnInvalidPageNumRequest() {
        assertThat(searchController.isValidPage(0), is(false));
    }

    @Test
    public void determinesThatMinus1IsAnInvalidPageNumRequest() {
        assertThat(searchController.isValidPage(-1), is(false));
    }

    // validate facets ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFacets() {
        List<String> filterQueries = new ArrayList<>();
        for (OntologyFieldSpec.Search searchable : OntologyFieldSpec.Search.values()) {
            filterQueries.add(searchable.name());
        }
        assertThat(searchController.isValidFacets(ontologyFieldSpec, filterQueries), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInFacets() {
        List<String> filterQueries = new ArrayList<>();
        filterQueries.add("aFieldThatDoesntExist");
        assertThat(searchController.isValidFacets(ontologyFieldSpec, filterQueries), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInFacets() {
        List<String> filterQueries = new ArrayList<>();

        // add a searchable, valid filter query
        filterQueries.add(OntologyFieldSpec.Search.id.name());
        assertThat(searchController.isValidFacets(ontologyFieldSpec, filterQueries), is(true));

        filterQueries.add("aFieldThatDoesntExist"); // then add a non-searchable field
        assertThat(searchController.isValidFacets(ontologyFieldSpec, filterQueries), is(false));
    }

    // validate filter queries ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFilterQueries() {
        List<String> filterQueries = new ArrayList<>();
        for (OntologyFieldSpec.Search searchable : OntologyFieldSpec.Search.values()) {
            filterQueries.add(searchable.name() + ":pretendValue");
        }
        assertThat(searchController.isValidFilterQueries(ontologyFieldSpec, filterQueries), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInAFilterQuery() {
        List<String> filterQueries = new ArrayList<>();
        filterQueries.add("aFieldThatDoesntExist:value");
        assertThat(searchController.isValidFilterQueries(ontologyFieldSpec, filterQueries), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInAFilterQuery() {
        List<String> filterQueries = new ArrayList<>();

        // add a searchable, valid filter query
        filterQueries.add(OntologyFieldSpec.Search.id.name() + ":value");
        assertThat(searchController.isValidFilterQueries(ontologyFieldSpec, filterQueries), is(true));

        filterQueries.add("aFieldThatDoesntExist:value"); // then add a non-searchable field
        assertThat(searchController.isValidFilterQueries(ontologyFieldSpec, filterQueries), is(false));
    }

    // a mock implementation of a class that requires a generic type parameter, SearchService<OBOTerm>
    private static class FakeSearchService implements SearchService<OBOTerm> {
        @Override public QueryResult<OBOTerm> findByQuery(QueryRequest request) {
            return null;
        }
    }
}