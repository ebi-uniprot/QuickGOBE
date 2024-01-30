package uk.ac.ebi.quickgo.client.controller;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

/**
 * Created 28/04/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class SearchControllerTest {
    @Mock
    private SearchService<OntologyTerm> searchService;

    @Mock
    private SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig;

    @Mock
    private FilterConverterFactory converterFactory;

    @Test
    void controllerInstantiationFailsOnNullSearchService() {
        assertThrows(IllegalArgumentException.class, () -> new SearchController(
                null,
                retrievalConfig,
                converterFactory));
    }

    @Test
    void controllerInstantiationFailsOnNullSearchableField() {
        assertThrows(IllegalArgumentException.class, () -> new SearchController(
                null,
                retrievalConfig,
                converterFactory));
    }

    @Test
    void controllerInstantiationFailsOnNullRetrievalConfig() {
        assertThrows(IllegalArgumentException.class, () -> new SearchController(
                searchService,
                null,
                converterFactory));
    }

    @Test
    void controllerInstantiationFailsOnNullConverterFactory() {
        assertThrows(IllegalArgumentException.class, () -> new SearchController(
                searchService,
                retrievalConfig,
                null));
    }
}