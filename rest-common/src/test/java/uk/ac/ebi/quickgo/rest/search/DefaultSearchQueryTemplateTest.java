package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.rest.search.query.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createCursorPage;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

/**
 * Created 11/04/16
 *
 * @author Edd
 */
public class DefaultSearchQueryTemplateTest {
    private static final String COLLECTION = SolrCollectionName.COLLECTION;
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
        int pageNumber = 11;
        int pageSize = 25;
        RegularPage expectedPage = new RegularPage(pageNumber, pageSize);

        Page abstractRetrievedPage = requestBuilder
                .setPage(expectedPage)
                .build()
                .getPage();

        assertThat(abstractRetrievedPage, is(instanceOf(RegularPage.class)));
        RegularPage regularPageRetrieved = (RegularPage) abstractRetrievedPage;
        assertThat(regularPageRetrieved.getPageNumber(), is(pageNumber));
        assertThat(regularPageRetrieved.getPageSize(), is(pageSize));
    }

    @Test
    public void queryRequestPageWasSetByDefault() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();

        Page abstractRetrievedPage = requestBuilder
                .build()
                .getPage();
        assertThat(abstractRetrievedPage, is(instanceOf(RegularPage.class)));
        RegularPage regularPageRetrieved = (RegularPage) abstractRetrievedPage;
        assertThat(regularPageRetrieved.getPageNumber(), is(DefaultSearchQueryTemplate.DEFAULT_PAGE_NUMBER));
        assertThat(regularPageRetrieved.getPageSize(), is(DefaultSearchQueryTemplate.DEFAULT_PAGE_SIZE));
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

    @Test
    public void queryRequestFirstCursorPageWasSet() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        int pageSize = 22;
        CursorPage expectedPage = CursorPage.createFirstCursorPage(pageSize);

        Page abstractRetrievedPage = requestBuilder
                .setPage(expectedPage)
                .build()
                .getPage();

        assertThat(abstractRetrievedPage, is(instanceOf(CursorPage.class)));
        CursorPage cursorPageRetrieved = (CursorPage) abstractRetrievedPage;
        assertThat(cursorPageRetrieved.getPageSize(), is(pageSize));
        assertThat(cursorPageRetrieved.getCursor(), is(CursorPage.FIRST_CURSOR));
    }

    @Test
    public void queryRequestCursorPageWasSet() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        int pageSize = 22;
        String cursor = "fakeCursor";
        CursorPage expectedPage = createCursorPage(cursor, pageSize);

        Page abstractRetrievedPage = requestBuilder
                .setPage(expectedPage)
                .build()
                .getPage();

        assertThat(abstractRetrievedPage, is(instanceOf(CursorPage.class)));
        CursorPage cursorPageRetrieved = (CursorPage) abstractRetrievedPage;
        assertThat(cursorPageRetrieved.getPageSize(), is(pageSize));
        assertThat(cursorPageRetrieved.getCursor(), is(cursor));
    }

    @Test
    public void queryRequestWithNoSortingShowsZeroSortCriteria() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        List<SortCriterion> sortCriteria = requestBuilder.build().getSortCriteria();

        assertThat(sortCriteria, hasSize(0));
    }

    @Test
    public void queryRequestSortCriterionWasSetViaBuilder() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        String sortField = "field";
        SortCriterion.SortOrder sortOrder = SortCriterion.SortOrder.DESC;
        List<SortCriterion> sortCriteria =
                requestBuilder
                        .addSortCriterion(sortField, sortOrder)
                        .build()
                        .getSortCriteria();

        assertThat(sortCriteria, hasSize(1));
        checkSortCriterion(sortCriteria.get(0), sortField, sortOrder);
    }

    @Test
    public void queryRequestSortCriteriaWereSetViaBuilder() {
        DefaultSearchQueryTemplate.Builder requestBuilder = createBuilder();
        String sortField0 = "sortField0";
        SortCriterion.SortOrder sortOrder0 = SortCriterion.SortOrder.ASC;
        String sortField1 = "sortField1";
        SortCriterion.SortOrder sortOrder1 = SortCriterion.SortOrder.DESC;
        String sortField2 = "sortField2";
        SortCriterion.SortOrder sortOrder2 = SortCriterion.SortOrder.ASC;

        List<SortCriterion> sortCriteria =
                requestBuilder
                        .addSortCriterion(sortField0, sortOrder0)
                        .addSortCriterion(sortField1, sortOrder1)
                        .addSortCriterion(sortField2, sortOrder2)
                        .build()
                        .getSortCriteria();

        assertThat(sortCriteria, hasSize(3));
        checkSortCriterion(sortCriteria.get(0), sortField0, sortOrder0);
        checkSortCriterion(sortCriteria.get(1), sortField1, sortOrder1);
        checkSortCriterion(sortCriteria.get(2), sortField2, sortOrder2);
    }

    @Test
    public void queryRequestSortCriteriaWasSetInTemplate() {
        String sortField0 = "sortField0";
        SortCriterion.SortOrder sortOrder0 = SortCriterion.SortOrder.ASC;
        String sortField1 = "sortField1";
        SortCriterion.SortOrder sortOrder1 = SortCriterion.SortOrder.DESC;
        String sortField2 = "sortField2";
        SortCriterion.SortOrder sortOrder2 = SortCriterion.SortOrder.ASC;

        defaultSearchQueryTemplate.addSortCriterion(sortField0, sortOrder0);
        defaultSearchQueryTemplate.addSortCriterion(sortField1, sortOrder1);
        defaultSearchQueryTemplate.addSortCriterion(sortField2, sortOrder2);

        List<SortCriterion> sortCriteria = createBuilder()
                .build()
                .getSortCriteria();

        assertThat(sortCriteria, hasSize(3));
        checkSortCriterion(sortCriteria.get(0), sortField0, sortOrder0);
        checkSortCriterion(sortCriteria.get(1), sortField1, sortOrder1);
        checkSortCriterion(sortCriteria.get(2), sortField2, sortOrder2);
    }

    @Test
    public void templatePageIsUsedByBuilder() {
        Page cursorPage = createFirstCursorPage(12345);
        defaultSearchQueryTemplate.setPage(cursorPage);
        QueryRequest request = defaultSearchQueryTemplate
                .newBuilder()
                .setQuery(new AllQuery())
                .setCollection(COLLECTION)
                .build();
        assertThat(request.getPage(), is(cursorPage));
    }

    private void checkSortCriterion(SortCriterion criterion, String field, SortCriterion.SortOrder order) {
        assertThat(criterion.getSortField().getField(), is(field));
        assertThat(criterion.getSortOrder(), is(order));
    }

    private DefaultSearchQueryTemplate.Builder createBuilder() {
        DefaultSearchQueryTemplate.Builder builder = defaultSearchQueryTemplate.newBuilder();
        builder.setQuery(this.query).setCollection(COLLECTION);
        return builder;
    }
}