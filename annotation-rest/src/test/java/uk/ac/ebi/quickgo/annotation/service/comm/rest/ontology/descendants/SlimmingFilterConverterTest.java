package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingFilterConverter;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyDescendants;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

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
public class SlimmingFilterConverterTest {
    private OntologyDescendants response;
    private SlimmingFilterConverter converter;

    @Before
    public void setUp() {
        response = new OntologyDescendants();
        response.setResults(new ArrayList<>());
        converter = new SlimmingFilterConverter();
    }

    @Test
    public void descendantsFromSingleResourceAreConvertedToQuickGOQuery() {
        String id1 = createGoId(1);
        String desc1 = createGoId(11);

        addResponseDescendant(id1, desc1);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(QuickGOQuery.createQuery(Searchable.GO_ID, desc1)));
        assertThat(convertedFilter.getFilterContext().isPresent(), is(true));
    }

    @Test
    public void differentDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = createGoId(1);
        String id2 = createGoId(2);
        String desc1 = createGoId(11);
        String desc2 = createGoId(22);

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc2);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                or(QuickGOQuery.createQuery(Searchable.GO_ID, desc1),
                        QuickGOQuery.createQuery(Searchable.GO_ID, desc2))));
        assertThat(convertedFilter.getFilterContext().isPresent(), is(true));
    }

    @Test
    public void sameDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = createGoId(1);
        String id2 = createGoId(2);
        String desc1 = createGoId(11);

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc1);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                QuickGOQuery.createQuery(Searchable.GO_ID, desc1)));
        assertThat(convertedFilter.getFilterContext().isPresent(), is(true));
    }

    @Test
    public void nullResultsMeansFilterEverything() {
        response.setResults(null);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
    }

    @Test
    public void emptyResultsMeansFilterEverything() {
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
    }

    @Test
    public void conversionContextContainsOneMapping() {
        String id1 = createGoId(1);
        String desc1 = createGoId(11);

        addResponseDescendant(id1, desc1);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(extractContextProperties(convertedFilter), hasEntry(desc1, singletonList(id1)));
    }

    @Test
    public void conversionContextContainsTwoMappings() {
        String id1 = createGoId(1);
        String id2 = createGoId(2);
        String desc1 = createGoId(11);
        String desc2 = createGoId(22);
        String desc3 = createGoId(33);

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id1, desc2);
        addResponseDescendant(id1, desc3);
        addResponseDescendant(id2, desc3);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(extractContextProperties(convertedFilter), hasEntry(desc1, singletonList(id1)));
        assertThat(extractContextProperties(convertedFilter), hasEntry(desc2, singletonList(id1)));
        assertThat(extractContextProperties(convertedFilter), hasEntry(desc3, asList(id1, id2)));
    }

    private Map<String, List<String>> extractContextProperties(ConvertedFilter<QuickGOQuery> convertedFilter) {
        SlimmingConversionInfo conversionInfo = convertedFilter.getFilterContext()
                .map(t -> t.get(SlimmingConversionInfo.class)
                        .orElse(new SlimmingConversionInfo()))
                .orElseThrow(IllegalStateException::new);

        return conversionInfo.getInfo();
    }

    private void addResponseDescendant(String termId, String descendantId) {
        for (OntologyDescendants.Result result : response.getResults()) {
            if (result.getId().equals(termId)) {
                result.getDescendants().add(descendantId);
                return;
            }
        }

        OntologyDescendants.Result newResult = new OntologyDescendants.Result();
        newResult.setId(termId);
        List<String> descList = new ArrayList<>();
        descList.add(descendantId);
        newResult.setDescendants(descList);
        response.getResults().add(newResult);
    }
}