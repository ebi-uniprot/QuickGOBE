package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created 28/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {
    @Mock
    private SearchService<OntologyTerm> searchService;

    @Mock
    private SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig;

    @Mock
    private FilterConverterFactory converterFactory;

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchService() {
        new SearchController(
                null,
                retrievalConfig,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchableField() {
        new SearchController(
                null,
                retrievalConfig,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullRetrievalConfig() {
        new SearchController(
                searchService,
                null,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullConverterFactory() {
        new SearchController(
                searchService,
                retrievalConfig,
                null);
    }
}