package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.Facet;
import uk.ac.ebi.quickgo.rest.search.query.FieldProjection;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created 11/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSearchQueryRequestBuilderTest {

    private static final String SEARCHABLE_FIELD = "searchable";
    private String query;
    private String id;
    private String startHighlight;
    private String endHighlight;
    private SearchableField searchableField = field -> field.equals(SEARCHABLE_FIELD) || field.equals(id);
    private List<String> returnedFields;
    private List<String> highlightedFields;
    private StringToQuickGOQueryConverter str2QueryConverter;

    @Before
    public void setUp() {
        this.id = "id";
        this.returnedFields = Arrays.asList(id, SEARCHABLE_FIELD);
        this.highlightedFields = Collections.singletonList(id);
        this.query = "query";
        this.str2QueryConverter = new StringToQuickGOQueryConverter(searchableField);
        this.startHighlight = "startHighlight";
        this.endHighlight = "endHighlight";
    }

    private DefaultSearchQueryRequestBuilder createSearchQueryRequestBuilder() {
        return new DefaultSearchQueryRequestBuilder(
                query,
                str2QueryConverter,
                searchableField,
                returnedFields,
                highlightedFields,
                startHighlight,
                endHighlight
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonSearchableFacetThrowsException() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        requestBuilder.checkFacets(Collections.singleton("nonExistingFacet"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonSearchableFilterThrowsException() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        requestBuilder.checkFilters(Collections.singleton("nonExistingFilterField:someQuery"));
    }

    @Test
    public void queryRequestQueryWasSet() {
        assertThat(createSearchQueryRequestBuilder().build().getQuery(), is(str2QueryConverter.convert(query)));
    }

    @Test
    public void queryRequestFacetWasSet() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        List<String> strFacets = new ArrayList<>();
        strFacets.add(id);
        requestBuilder.addFacets(strFacets);

        List<Facet> modelFacets = new ArrayList<>();
        modelFacets.add(new Facet(id));

        assertThat(requestBuilder.build().getFacets(), is(modelFacets));
    }

    @Test
    public void queryRequestHasNoFacetByDefault() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        assertThat(requestBuilder.build().getFacets(), is(emptyIterable()));
    }

    @Test
    public void queryRequestFilterWasSet() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        List<String> strFilters = new ArrayList<>();
        String filterQueryStr = id + ":value";
        strFilters.add(filterQueryStr);
        requestBuilder.addFilters(strFilters);

        List<QuickGOQuery> modelFilters = new ArrayList<>();
        modelFilters.add(str2QueryConverter.convert(filterQueryStr));

        assertThat(requestBuilder.build().getFilters(), is(modelFilters));
    }

    @Test
    public void queryRequestHasNoFiltersByDefault() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        assertThat(requestBuilder.build().getFilters(), is(emptyIterable()));
    }

    @Test
    public void queryRequestPageWasSetExplicitly() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        int page = 11;
        requestBuilder.setPage(page);
        assertThat(requestBuilder.build().getPage().getPageNumber(), is(page));
    }

    @Test
    public void queryRequestPageWasSetByDefault() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        assertThat(requestBuilder.build().getPage().getPageNumber(), is(DefaultSearchQueryRequestBuilder.DEFAULT_PAGE_NUMBER));
    }

    @Test
    public void queryRequestPageSizeWasSetExplicitly() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        int pageSize = 11;
        requestBuilder.setPageSize(pageSize);
        assertThat(requestBuilder.build().getPage().getPageSize(), is(pageSize));
    }

    @Test
    public void queryRequestPageSizeWasSetByDefault() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        assertThat(requestBuilder.build().getPage().getPageSize(), is(DefaultSearchQueryRequestBuilder.DEFAULT_PAGE_SIZE));
    }

    @Test
    public void queryRequestHighlightingWasTurnedOn() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        requestBuilder.useHighlighting(true);

        QueryRequest queryRequest = requestBuilder.build();
        assertThat(queryRequest.getHighlightedFields(), is(notNullValue()));
        assertThat(queryRequest.getHighlightStartDelim(), is(notNullValue()));
        assertThat(queryRequest.getHighlightEndDelim(), is(notNullValue()));
    }

    @Test
    public void queryRequestHighlightingWasTurnedOff() {
        DefaultSearchQueryRequestBuilder requestBuilder = createSearchQueryRequestBuilder();
        requestBuilder.useHighlighting(false);

        QueryRequest queryRequest = requestBuilder.build();
        assertThat(queryRequest.getHighlightedFields(), is(emptyIterable()));
        assertThat(queryRequest.getHighlightStartDelim(), is(nullValue()));
        assertThat(queryRequest.getHighlightEndDelim(), is(nullValue()));
    }

    @Test
    public void queryRequestHighlightingIsOffByDefault() {
        QueryRequest queryRequest = createSearchQueryRequestBuilder().build();
        assertThat(queryRequest.getHighlightedFields(), is(emptyIterable()));
        assertThat(queryRequest.getHighlightStartDelim(), is(nullValue()));
        assertThat(queryRequest.getHighlightEndDelim(), is(nullValue()));
    }

    @Test
    public void queryRequestReturnedFieldsIsSetByDefault() {
        QueryRequest queryRequest = createSearchQueryRequestBuilder().build();

        List<FieldProjection> fieldProjections = new ArrayList<>();
        this.returnedFields.stream().map(FieldProjection::new).forEach(fieldProjections::add);

        assertThat(queryRequest.getProjectedFields(), is(fieldProjections));
    }
}