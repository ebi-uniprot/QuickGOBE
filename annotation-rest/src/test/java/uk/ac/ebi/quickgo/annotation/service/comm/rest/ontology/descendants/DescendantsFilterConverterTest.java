package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.DescendantsFilterConverter;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * Created 10/08/16
 * @author Edd
 */
public class DescendantsFilterConverterTest {
    private ConvertedOntologyFilter response;
    private DescendantsFilterConverter converter;

    @Before
    public void setUp() {
        response = new ConvertedOntologyFilter();
        response.setResults(new ArrayList<>());
        converter = new DescendantsFilterConverter();
    }

    @Test
    public void descendantsFromSingleResourceAreConvertedToQuickGOQuery() {
        String id1 = "id1";
        String desc1 = "desc1";

        addResponseDescendant(id1, desc1);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc1)));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void differentDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = "id1";
        String id2 = "id2";
        String desc1 = "desc1";
        String desc2 = "desc2";

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc2);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                or(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc1),
                        QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc2))));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void sameDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = "id1";
        String id2 = "id2";
        String desc1 = "desc1";

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc1);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc1)));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void nullResultsMeansFilterEverything() {
        response.setResults(null);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void emptyResultsMeansFilterEverything() {
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    private void addResponseDescendant(String termId, String descendantId) {
        for (ConvertedOntologyFilter.Result result : response.getResults()) {
            if (result.getId().equals(termId)) {
                result.getDescendants().add(descendantId);
                return;
            }
        }

        ConvertedOntologyFilter.Result newResult = new ConvertedOntologyFilter.Result();
        newResult.setId(termId);
        List<String> descList = new ArrayList<>();
        descList.add(descendantId);
        newResult.setDescendants(descList);
        response.getResults().add(newResult);
    }
}