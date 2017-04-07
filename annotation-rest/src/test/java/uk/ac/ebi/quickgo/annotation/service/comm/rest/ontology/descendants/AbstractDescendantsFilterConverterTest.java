package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.DescendantsFilterConverter;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyDescendants;
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
 * Created by edd on 02/11/2016.
 */
public abstract class AbstractDescendantsFilterConverterTest {
    private OntologyDescendants response;
    private DescendantsFilterConverter converter;
    String field;

    @Before
    public void setUp() {
        response = new OntologyDescendants();
        response.setResults(new ArrayList<>());
        converter = new DescendantsFilterConverter();
    }

    public abstract String ontologyId(int id);

    @Test
    public void descendantsFromSingleResourceAreConvertedToQuickGOQuery() {
        String id1 = ontologyId(1);
        String desc1 = ontologyId(2);

        addResponseDescendant(id1, desc1);
        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(QuickGOQuery.createQuery(field, desc1)));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void differentDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = ontologyId(1);
        String id2 = ontologyId(2);
        String desc1 = ontologyId(11);
        String desc2 = ontologyId(22);

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc2);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                or(QuickGOQuery.createQuery(field, desc1),
                        QuickGOQuery.createQuery(field, desc2))));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void sameDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = ontologyId(1);
        String id2 = ontologyId(2);
        String desc1 = ontologyId(11);

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc1);

        ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(
                QuickGOQuery.createQuery(field, desc1)));
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