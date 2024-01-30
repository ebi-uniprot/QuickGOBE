package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingFilterConverter;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGoId;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Searchable;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * Created 10/08/16
 * @author Edd
 */
class SlimmingFilterConverterTest {
    private OntologyRelatives response;
    private SlimmingFilterConverter converter;

    @BeforeEach
    void setUp() {
        response = new OntologyRelatives();
        response.setResults(new ArrayList<>());
        converter = new SlimmingFilterConverter();
    }

    @Test
    void descendantsFromSingleResourceAreConvertedToQuickGOQuery() {
        String id1 = createGoId(1);
        String slim1 = createGoId(11);

        addResponseSlimsTo(id1, slim1);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(QuickGOQuery.createQuery(Searchable.GO_ID, id1)));
        assertThat(convertedFilter.getFilterContext().isPresent(), is(true));
    }

    @Test
    void differentDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = createGoId(1);
        String id2 = createGoId(2);
        String slim1 = createGoId(11);
        String slim2 = createGoId(22);

        addResponseSlimsTo(id1, slim1);
        addResponseSlimsTo(id2, slim2);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                or(QuickGOQuery.createQuery(Searchable.GO_ID, id1),
                        QuickGOQuery.createQuery(Searchable.GO_ID, id2))));
        assertThat(convertedFilter.getFilterContext().isPresent(), is(true));
    }

    @Test
    void sameDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = createGoId(1);
        String id2 = createGoId(2);
        String slim1 = createGoId(11);

        addResponseSlimsTo(id1, slim1);
        addResponseSlimsTo(id2, slim1);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(),
                is(
                        or(QuickGOQuery.createQuery(Searchable.GO_ID, id1),
                                QuickGOQuery.createQuery(Searchable.GO_ID, id2))));
        assertThat(convertedFilter.getFilterContext().isPresent(), is(true));
    }

    @Test
    void nullResultsMeansFilterEverything() {
        response.setResults(null);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
    }

    @Test
    void emptyResultsMeansFilterEverything() {
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
    }

    @Test
    void conversionContextContainsOneMapping() {
        String id1 = createGoId(1);
        String slim1 = createGoId(11);

        addResponseSlimsTo(id1, slim1);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(extractContextProperties(convertedFilter), hasEntry(id1, singletonList(slim1)));
    }

    @Test
    void conversionContextContainsTwoMappings() {
        String id1 = createGoId(1);
        String id2 = createGoId(2);
        String slim1 = createGoId(11);
        String slim2 = createGoId(22);
        String slim3 = createGoId(33);

        addResponseSlimsTo(id1, slim1);
        addResponseSlimsTo(id1, slim2);
        addResponseSlimsTo(id1, slim3);
        addResponseSlimsTo(id2, slim3);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(extractContextProperties(convertedFilter), hasEntry(id1, asList(slim1, slim2, slim3)));
        assertThat(extractContextProperties(convertedFilter), hasEntry(id2, singletonList(slim3)));
    }

    private Map<String, List<String>> extractContextProperties(ConvertedFilter<QuickGOQuery> convertedFilter) {
        SlimmingConversionInfo conversionInfo = convertedFilter.getFilterContext()
                .map(t -> t.get(SlimmingConversionInfo.class)
                        .orElse(new SlimmingConversionInfo()))
                .orElseThrow(IllegalStateException::new);

        return conversionInfo.getInfo();
    }

    private void addResponseSlimsTo(String termId, String slimId) {
        for (OntologyRelatives.Result result : response.getResults()) {
            if (result.getSlimsFromId().equals(termId)) {
                result.getSlimsToIds().add(slimId);
                return;
            }
        }

        OntologyRelatives.Result newResult = new OntologyRelatives.Result();
        newResult.setSlimsFromId(termId);
        List<String> slimList = new ArrayList<>();
        slimList.add(slimId);
        newResult.setSlimsToIds(slimList);
        response.getResults().add(newResult);
    }
}