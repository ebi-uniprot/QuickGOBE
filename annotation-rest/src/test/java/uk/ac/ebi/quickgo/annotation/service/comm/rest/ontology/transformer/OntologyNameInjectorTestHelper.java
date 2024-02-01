package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author Tony Wardell
 * Date: 24/10/2017
 * Time: 11:37
 * Created with IntelliJ IDEA.
 */
public class OntologyNameInjectorTestHelper {
    public static final String TEST_GO_ID = "go id in test";
    public static final BasicOntology basicOntology = createBasicOntology();

    private static BasicOntology createBasicOntology() {
        BasicOntology ontology = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();
        BasicOntology.Result result = new BasicOntology.Result();
        result.setId("ID:1");
        result.setName("Name:1");
        results.add(result);
        ontology.setResults(results);
        return ontology;
    }

    public static void buildFilterRequestSuccessfully(FilterRequest filterRequest, AbstractValueInjector nameInjector){
        assertThat(filterRequest.getProperties(), hasEntry(nameInjector.getId(), emptyList()));
        assertThat(filterRequest.getProperties(), hasEntry("goId", singletonList(TEST_GO_ID)));
    }

    public static void injectValueSuccessfully(String goName){
        assertThat(goName, is(not(nullValue())));
        assertThat(goName, is(basicOntology.getResults().get(0).getName()));
    }
}
