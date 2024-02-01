package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyRestConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologySpecifier;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests available components of the {@link OBOController} class.
 *
 * Created 16/02/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class OBOControllerTest {
    private static final Pattern ID_FORMAT = Pattern.compile("id[0-9]");
    private static final int MAX_PAGE_SIZE = 30;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final LocalTime START_TIME = LocalTime.of(18, 0);
    private static final LocalTime END_TIME = LocalTime.of(17, 0);
    private static Function<LocalTime, Long> remainingCacheCalculator = (t) -> 1L;
    @Mock
    private SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig;
    @Mock
    private OBOControllerValidationHelper oboControllerValidationHelper;
    @Mock
    private OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig;
    @Mock
    private OntologyService<FakeOBOTerm> ontologyService;
    @Mock
    private SearchService<OBOTerm> searchService;
    @Mock
    private SearchableField searchableField;
    @Mock
    private GraphImageService graphImageService;
    @Mock
    private HttpHeadersProvider headersProvider;
    private OBOController<FakeOBOTerm> controller;
    private List<OntologyRelationType> validRelations = new ArrayList<>();
    private static final OntologySpecifier ontologySpecifier = new OntologySpecifier(OntologyType.GO, new ArrayList<>());

    private static OBOController<FakeOBOTerm> createOBOController(
            final OntologyService<FakeOBOTerm> ontologyService, final SearchService<OBOTerm> searchService,
            final SearchableField searchableField,
            final SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig,
            final GraphImageService graphImageService,
            OBOControllerValidationHelper oboControllerValidationHelper,
            OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig,
            OntologySpecifier ontologySpecifier,
            HttpHeadersProvider headersProvider) {
        return new OBOController<FakeOBOTerm>(ontologyService,
                                              searchService,
                                              searchableField,
                                              retrievalConfig,
                                              graphImageService,
                                              oboControllerValidationHelper,
                                              ontologyPagingConfig,
                                              ontologySpecifier,
                                              headersProvider) {
        };
    }

    @BeforeEach
    void setUp() {

        this.controller = createOBOController(ontologyService,
                                              searchService,
                                              searchableField,
                                              retrievalConfig,
                                              graphImageService,
                                              oboControllerValidationHelper,
                                              ontologyPagingConfig,
                                              ontologySpecifier,
                                              headersProvider);
    }

    @Test
    void termsResponseForNullListContainsZeroResults() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getResultsResponse(null);
        assertThat(termsResponse.getBody().getNumberOfHits(), is(0L));
        assertThat(termsResponse.getBody().getResults(), is(empty()));
    }

    @Test
    void termsResponseForEmptyListContainsZeroResults() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getResultsResponse(Collections.emptyList());
        assertThat(termsResponse.getBody().getNumberOfHits(), is(0L));
        assertThat(termsResponse.getBody().getResults(), is(empty()));
    }

    @Test
    void termsResponseForListOfOneContainsOneResult() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getResultsResponse(Collections
                                                                                                       .singletonList(
                                                                                                               new FakeOBOTerm()));
        assertThat(termsResponse.getBody().getNumberOfHits(), is(1L));
        assertThat(termsResponse.getBody().getResults().size(), is(1));
    }

    @Test
    void controllerInstantiationFailsOnNullOntologyService() {
        assertThrows(IllegalArgumentException.class, () -> createOBOController(
                null,
                searchService,
                searchableField,
                retrievalConfig,
                graphImageService,
                oboControllerValidationHelper,
                ontologyPagingConfig,
                ontologySpecifier,
                headersProvider));
    }

    @Test
    void controllerInstantiationFailsOnNullSearchService() {
        assertThrows(IllegalArgumentException.class, () -> createOBOController(ontologyService,
                null,
                searchableField,
                retrievalConfig,
                graphImageService,
                oboControllerValidationHelper,
                ontologyPagingConfig,
                ontologySpecifier,
                headersProvider));
    }

    @Test
    void controllerInstantiationFailsOnNullSearchableField() {
        assertThrows(IllegalArgumentException.class, () -> createOBOController(ontologyService,
                searchService,
                null,
                retrievalConfig,
                graphImageService,
                oboControllerValidationHelper,
                ontologyPagingConfig,
                ontologySpecifier,
                headersProvider));
    }

    @Test
    void controllerInstantiationFailsOnNullRetrievalConfig() {
        assertThrows(IllegalArgumentException.class, () -> createOBOController(ontologyService,
                searchService,
                searchableField,
                null,
                graphImageService,
                oboControllerValidationHelper,
                ontologyPagingConfig,
                ontologySpecifier,
                headersProvider));
    }

    @Test
    void controllerInstantiationFailsOnNullGraphImageService() {
        assertThrows(IllegalArgumentException.class, () -> createOBOController(ontologyService,
                searchService,
                searchableField,
                retrievalConfig,
                null,
                oboControllerValidationHelper,
                ontologyPagingConfig,
                ontologySpecifier,
                headersProvider));
    }

    @Test
    void controllerInstantiationFailsOnNullValidationHelper() {
        assertThrows(IllegalArgumentException.class, () -> createOBOController(ontologyService, searchService,
          searchableField, retrievalConfig, graphImageService, null, ontologyPagingConfig, ontologySpecifier, headersProvider));
    }

    @Test
    void controllerInstantiationFailsOnNullPagingConfig() {
        assertThrows(IllegalArgumentException.class, () ->
            createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                    oboControllerValidationHelper, null, ontologySpecifier, headersProvider)
        );
    }

    @Test
    void controllerInstantiationFailsOnNullOntologySpecifier() {
        assertThrows(IllegalArgumentException.class, () ->
            createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                    oboControllerValidationHelper, ontologyPagingConfig, null, headersProvider)
        );
    }

    @Test
    void controllerInstantiationFailsOnNullHeaderProvider() {
        assertThrows(IllegalArgumentException.class, () ->
            createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                    oboControllerValidationHelper, ontologyPagingConfig, ontologySpecifier, null)
        );
    }

    private static class FakeOBOTerm extends OBOTerm {}

}
