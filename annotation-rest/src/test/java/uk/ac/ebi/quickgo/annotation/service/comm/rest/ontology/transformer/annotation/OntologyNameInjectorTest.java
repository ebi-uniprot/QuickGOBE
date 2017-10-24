package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjectorTestHelper
        .TEST_GO_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjectorTestHelper
        .basicOntology;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjectorTestHelper
        .buildFilterRequestSuccessfully;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.OntologyNameInjectorTestHelper
        .injectValueSuccessfully;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.OntologyNameInjector.GO_NAME;

/**
 * Created 11/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologyNameInjectorTest {
    private OntologyNameInjector nameInjector;
    private Annotation annotation;

    @Before
    public void setUp() {
        nameInjector = new OntologyNameInjector();
        annotation = new Annotation();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is(GO_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(basicOntology);
        assertThat(annotation.goName, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, annotation);

        injectValueSuccessfully(annotation.goName);
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        annotation.goId = TEST_GO_ID;
        FilterRequest filterRequest = nameInjector.buildFilterRequest(annotation);

        buildFilterRequestSuccessfully(filterRequest, nameInjector);
    }
}
