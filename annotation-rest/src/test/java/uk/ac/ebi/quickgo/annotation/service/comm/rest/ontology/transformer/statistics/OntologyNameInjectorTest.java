package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjectorTestHelper.*;
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
    private StatisticsValue statisticsValue;

    @Before
    public void setUp() {
        nameInjector = new OntologyNameInjector();
        statisticsValue = new StatisticsValue(TEST_GO_ID, 5, 10L);
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is(GO_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(basicOntology);
        assertThat(statisticsValue.getName(), is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, statisticsValue);

        injectValueSuccessfully(statisticsValue.getName());
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        FilterRequest filterRequest = nameInjector.buildFilterRequest(statisticsValue);

        buildFilterRequestSuccessfully(filterRequest, nameInjector);
    }
}
