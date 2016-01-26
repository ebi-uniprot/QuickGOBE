package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyFields;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;
import uk.ac.ebi.quickgo.service.search.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for the {@link SearchController}. Primarily tests
 * user request validation logic. Functional / integration tests are covered by
 * {@link OntologySearchIT} and {@link OntologyUserQueryScoringIT}.
 *
 * Created 25/01/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologySearchControllerTest {
    private SearchController searchController;
    private OntologyFieldSpec ontologyFieldSpec;

    @Mock
    private SearchService<OBOTerm> ontologyServiceMock;

    @Before
    public void setUp() {
        this.ontologyFieldSpec = new OntologyFieldSpec();
        this.searchController = new SearchController(ontologyServiceMock, ontologyFieldSpec);
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
        List<String> facets = OntologyFields.Searchable.VALUES.stream().collect(Collectors.toList());
        assertThat(searchController.isValidFacets(ontologyFieldSpec, facets), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInFacets() {
        List<String> facets = new ArrayList<>();
        facets.add("aFieldThatDoesntExist");
        assertThat(searchController.isValidFacets(ontologyFieldSpec, facets), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInFacets() {
        List<String> facets = new ArrayList<>();

        // add a searchable, valid filter query
        facets.add(OntologyFields.Searchable.ID);
        assertThat(searchController.isValidFacets(ontologyFieldSpec, facets), is(true));

        facets.add("aFieldThatDoesntExist"); // then add a non-searchable field
        assertThat(searchController.isValidFacets(ontologyFieldSpec, facets), is(false));
    }

    // validate filter queries ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFilterQueries() {
        List<String> filterQueries = OntologyFields.Searchable.VALUES.stream()
                .map(field -> field +":pretendValue")
                .collect(Collectors.toList());

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
        filterQueries.add(OntologyFields.Searchable.ID + ":value");
        assertThat(searchController.isValidFilterQueries(ontologyFieldSpec, filterQueries), is(true));

        filterQueries.add("aFieldThatDoesntExist:value"); // then add a non-searchable field
        assertThat(searchController.isValidFilterQueries(ontologyFieldSpec, filterQueries), is(false));
    }
}