package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics.OntologyNameInjector.GO_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics.OntologyNameInjector
        .GO_NAME;

/**
 * @author Tony Wardell
 * Date: 06/10/2017
 * Time: 16:08
 * Created with IntelliJ IDEA.
 */
public class OntologyNameInjectorTest {

    private OntologyNameInjector nameInjector;

    @Before
    public void setUp() {
        nameInjector = new OntologyNameInjector();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), Is.is(GO_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        BasicOntology mockedResponse = createBasicOntology();
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        String goId = "go id in test";
        StatisticsValue statisticsValue = new StatisticsValue(goId, 5, 10L);
        assertThat(statisticsValue.getName(), Is.is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, statisticsValue);

        assertThat(statisticsValue.getName(), Is.is(not(nullValue())));
        assertThat(statisticsValue.getName(), Is.is(mockedResponse.getResults().get(0).getName()));
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        String goId = "go id in test";
        StatisticsValue statisticsValue = new StatisticsValue(goId, 5, 10L);
        FilterRequest filterRequest = nameInjector.buildFilterRequest(statisticsValue);

        assertThat(filterRequest.getProperties(), hasEntry(nameInjector.getId(), emptyList()));
        assertThat(filterRequest.getProperties(), hasEntry(GO_ID, singletonList(goId)));
    }

    private BasicOntology createBasicOntology() {
        BasicOntology ontology = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();
        BasicOntology.Result result = new BasicOntology.Result();
        result.setId("ID:1");
        result.setName("Name:1");
        results.add(result);
        ontology.setResults(results);
        return ontology;
    }
}
