package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.common.SearchableField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.*;

/**
 * Unit tests for the {@link SearchDispatcher}. Primarily tests
 * user request validation logic. Functional / integration tests are covered by
 * (higher-level) callers of {@link SearchDispatcher}'s methods.
 *
 * Created 07/04/16
 * @author Edd
 */
public class SearchDispatcherTest {
    private static class MockSearchableField implements SearchableField {

        private static final String SEARCHABLE_FIELD = "searchableField";

        @Override public boolean isSearchable(String field) {
            return field.equals(SEARCHABLE_FIELD);
        }

        @Override public Stream<String> searchableFields() {
            return Stream.empty();
        }
    }

    private MockSearchableField searchableField;

    @Before
    public void setUp() {
        this.searchableField = new MockSearchableField();
    }

    // validate query ----------------------------------------------
    @Test
    public void determinesThatQueryIsValidForProcessing() {
        assertThat(isValidQuery("query"), is(true));
    }

    @Test
    public void determinesThatNullQueryIsInvalidForProcessing() {
        assertThat(isValidQuery(null), is(false));
    }

    @Test
    public void determinesThatEmptyQueryIsInvalidForProcessing() {
        assertThat(isValidQuery(""), is(false));
    }

    // validate row num in request ----------------------------------------------
    @Test
    public void determinesThat1IsAValidRowNumsRequest() {
        assertThat(isValidNumRows(1), is(true));
    }

    @Test
    public void determinesThat0IsAnInvalidRowNumsRequest() {
        assertThat(isValidNumRows(0), is(false));
    }

    @Test
    public void determinesThatMinus1IsAnInvalidRowNumsRequest() {
        assertThat(isValidNumRows(-1), is(false));
    }

    // validate page num in request ----------------------------------------------
    @Test
    public void determinesThat1IsAValidPageNumRequest() {
        assertThat(isValidPage(1), is(true));
    }

    @Test
    public void determinesThat0IsAnInvalidPageNumRequest() {
        assertThat(isValidPage(0), is(false));
    }

    @Test
    public void determinesThatMinus1IsAnInvalidPageNumRequest() {
        assertThat(isValidPage(-1), is(false));
    }

    // validate facets ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFacets() {
        List<String> facets = Collections.singletonList(MockSearchableField.SEARCHABLE_FIELD);

        assertThat(isValidFacets(searchableField, facets), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInFacets() {
        List<String> facets = new ArrayList<>();
        facets.add("aFieldThatDoesntExist");
        assertThat(isValidFacets(searchableField, facets), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInFacets() {
        List<String> facets = new ArrayList<>();

        // add a searchable, valid filter query
        facets.add(MockSearchableField.SEARCHABLE_FIELD);
        assertThat(isValidFacets(searchableField, facets), is(true));

        facets.add("aFieldThatDoesntExist"); // then add a non-searchable field
        assertThat(isValidFacets(searchableField, facets), is(false));
    }

    // validate filter queries ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFilterQueries() {
        List<String> filterQueries = Stream.of(MockSearchableField.SEARCHABLE_FIELD)
                .map(field -> field + ":pretendValue")
                .collect(Collectors.toList());

        assertThat(isValidFilterQueries(searchableField, filterQueries), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInAFilterQuery() {
        List<String> filterQueries = new ArrayList<>();
        filterQueries.add("aFieldThatDoesntExist:value");
        assertThat(isValidFilterQueries(searchableField, filterQueries), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInAFilterQuery() {
        List<String> filterQueries = new ArrayList<>();

        // add a searchable, valid filter query
        filterQueries.add(MockSearchableField.SEARCHABLE_FIELD + ":value");
        assertThat(isValidFilterQueries(searchableField, filterQueries), is(true));

        filterQueries.add("aFieldThatDoesntExist:value"); // then add a non-searchable field
        assertThat(isValidFilterQueries(searchableField, filterQueries), is(false));
    }
}