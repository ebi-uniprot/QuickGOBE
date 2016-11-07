package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.search.query.Facet;
import uk.ac.ebi.quickgo.rest.search.query.FieldProjection;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created 11/04/16
 * @author Edd
 */
public class DefaultSearchQueryTemplateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String START_HIGHLIGHT = "startHighlight";
    private static final String END_HIGHLIGHT = "endHighlight";
    private static final String QUERY = "query";
    private static final String ID = "id";

    private QuickGOQuery query;
    private String id;
    private List<String> returnedFields;
    private DefaultSearchQueryTemplate defaultSearchQueryTemplate;

    @Before
    public void setUp() {
        this.id = ID;

        List<String> highlightFields = Collections.singletonList(id);

        this.returnedFields = Collections.singletonList(id);
        this.query = QuickGOQuery.createQuery(QUERY);

        this.defaultSearchQueryTemplate = new DefaultSearchQueryTemplate();

        this.defaultSearchQueryTemplate.setHighlighting(highlightFields, START_HIGHLIGHT, END_HIGHLIGHT);
        this.defaultSearchQueryTemplate.setReturnedFields(returnedFields);
    }

    @Test
    public void queryRequestQueryWasSet() {
        assertThat(createBuilder().build().getQuery(), is(query));
    }

    @Test
    public void queryRequestFacetWasSet() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        List<String> strFacets = new ArrayList<>();
        strFacets.add(id);
        requestBuilder.addFacets(strFacets);

        List<Facet> modelFacets = new ArrayList<>();
        modelFacets.add(new Facet(id));

        assertThat(requestBuilder.build().getFacets(), is(modelFacets));
    }

    @Test
    public void queryRequestHasNoFacetByDefault() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        assertThat(requestBuilder.build().getFacets(), is(emptyIterable()));
    }

    @Test
    public void queryRequestFilterWasSet() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();

        QuickGOQuery filterQuery = QuickGOQuery.createQuery(id, "value");
        List<QuickGOQuery> filterQueries =
                Collections.singletonList(filterQuery);

        requestBuilder.addFilters(filterQueries);

        List<QuickGOQuery> modelFilters = Collections.singletonList(filterQuery);

        assertThat(requestBuilder.build().getFilters(), is(modelFilters));
    }

    @Test
    public void queryRequestHasNoFiltersByDefault() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        assertThat(requestBuilder.build().getFilters(), is(emptyIterable()));
    }

    @Test
    public void queryRequestPageWasSetExplicitly() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        int page = 11;
        requestBuilder.setPage(page);
        assertThat(requestBuilder.build().getPage().getPageNumber(), is(page));
    }

    @Test
    public void queryRequestPageWasSetByDefault() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        assertThat(requestBuilder.build().getPage().getPageNumber(),
                is(DefaultSearchQueryTemplate.DEFAULT_PAGE_NUMBER));
    }

    @Test
    public void queryRequestPageSizeWasSetExplicitly() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        int pageSize = 11;
        requestBuilder.setPageSize(pageSize);
        assertThat(requestBuilder.build().getPage().getPageSize(), is(pageSize));
    }

    @Test
    public void queryRequestPageSizeWasSetByDefault() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        assertThat(requestBuilder.build().getPage().getPageSize(), is(DefaultSearchQueryTemplate.DEFAULT_PAGE_SIZE));
    }

    @Test
    public void queryRequestHighlightingWasTurnedOn() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        requestBuilder.useHighlighting(true);

        QueryRequest queryRequest = requestBuilder.build();
        assertThat(queryRequest.getHighlightedFields(), is(notNullValue()));
        assertThat(queryRequest.getHighlightStartDelim(), is(notNullValue()));
        assertThat(queryRequest.getHighlightEndDelim(), is(notNullValue()));
    }

    @Test
    public void queryRequestHighlightingWasTurnedOff() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        requestBuilder.useHighlighting(false);

        QueryRequest queryRequest = requestBuilder.build();
        assertThat(queryRequest.getHighlightedFields(), is(emptyIterable()));
        assertThat(queryRequest.getHighlightStartDelim(), is(nullValue()));
        assertThat(queryRequest.getHighlightEndDelim(), is(nullValue()));
    }

    @Test
    public void queryRequestHighlightingIsOffByDefault() {
        QueryRequest queryRequest = createBuilder().build();
        assertThat(queryRequest.getHighlightedFields(), is(emptyIterable()));
        assertThat(queryRequest.getHighlightStartDelim(), is(nullValue()));
        assertThat(queryRequest.getHighlightEndDelim(), is(nullValue()));
    }

    @Test
    public void queryRequestReturnedFieldsIsSetByDefault() {
        QueryRequest queryRequest = createBuilder().build();

        List<FieldProjection> fieldProjections = new ArrayList<>();
        this.returnedFields.stream().map(FieldProjection::new).forEach(fieldProjections::add);

        assertThat(queryRequest.getProjectedFields(), is(fieldProjections));
    }

    private DefaultSearchQueryTemplate.Builder createBuilder() {
        DefaultSearchQueryTemplate.Builder builder = defaultSearchQueryTemplate.newBuilder();
        builder.setQuery(this.query);
        return builder;
    }
}