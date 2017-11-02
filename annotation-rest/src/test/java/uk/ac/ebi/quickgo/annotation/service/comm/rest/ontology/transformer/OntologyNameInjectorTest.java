package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjector.GO_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjector.GO_NAME;

/**
 * Created 11/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologyNameInjectorTest {
    private OntologyNameInjector nameInjector;

    @Before
    public void setUp() {
        nameInjector = new OntologyNameInjector();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is(GO_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        BasicOntology mockedResponse = createBasicOntology();
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        Annotation annotation = new Annotation();
        assertThat(annotation.goName, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, annotation);

        assertThat(annotation.goName, is(not(nullValue())));
        assertThat(annotation.goName, is(mockedResponse.getResults().get(0).getName()));
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        Annotation annotation = new Annotation();
        String goId = "go id in test";
        annotation.goId = goId;
        FilterRequest filterRequest = nameInjector.buildFilterRequest(annotation);

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