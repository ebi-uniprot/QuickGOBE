package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

/**
 * Created 06/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologyResultsTransformerTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullRESTFilterConverterFactoryCausesException() {
        new OntologyResultsTransformer(null, Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldInjectorsCausesException() {
        RESTFilterConverterFactory mockRESTFilterConverterFactory = mock(RESTFilterConverterFactory.class);
        new OntologyResultsTransformer(mockRESTFilterConverterFactory, null);
    }
}