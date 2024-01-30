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
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.EvidenceNameInjectorTestHelper.*;

/**
 * Created 11/04/17
 * @author Tony Wardell
 */
class EvidenceNameInjectorTest {
    private EvidenceNameInjector nameInjector;
    private CompletableValue completableValue;

    @BeforeEach
    void setUp() {
        nameInjector = new EvidenceNameInjector();
        completableValue = new CompletableValue(TEST_EVIDENCE_ID);
    }

    @Test
    void injectorIdIsEvidenceName() {
        assertThat(nameInjector.getId(), is("evidenceName"));
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
