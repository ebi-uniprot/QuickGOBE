package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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


/**
 * Created 11/04/17
 * @author Edd
 */
class OntologyNameInjectorTest {
    private OntologyNameInjector nameInjector;
    private CompletableValue completableValue;

    @BeforeEach
    void setUp() {
        nameInjector = new OntologyNameInjector();
        completableValue = new CompletableValue(TEST_GO_ID);
    }

    @Test
    void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is("goName"));
    }

    @Test
    void responseValueIsInjectedToAnnotation() {
        ConvertedFilter<BasicOntology> stubConvertedFilter = new ConvertedFilter<>(basicOntology);
        assertThat(completableValue.value, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, completableValue);

        injectValueSuccessfully(completableValue.value);
    }

    @Test
    void correctFilterRequestIsBuilt() {
        FilterRequest filterRequest = nameInjector.buildFilterRequest(completableValue);

        buildFilterRequestSuccessfully(filterRequest, nameInjector);
    }
}
