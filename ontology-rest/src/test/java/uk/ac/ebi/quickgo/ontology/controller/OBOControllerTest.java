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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests available components of the {@link OBOController} class.
 *
 * Created 16/02/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OBOControllerTest {
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
    private static OntologySpecifier ontologySpecifier = new OntologySpecifier(OntologyType.GO, new ArrayList<>());

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

    @Before
    public void setUp() {

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
    public void termsResponseForNullListContainsZeroResults() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getResultsResponse(null);
        assertThat(termsResponse.getBody().getNumberOfHits(), is(0L));
        assertThat(termsResponse.getBody().getResults(), is(empty()));
    }

    @Test
    public void termsResponseForEmptyListContainsZeroResults() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getResultsResponse(Collections.emptyList());
        assertThat(termsResponse.getBody().getNumberOfHits(), is(0L));
        assertThat(termsResponse.getBody().getResults(), is(empty()));
    }

    @Test
    public void termsResponseForListOfOneContainsOneResult() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getResultsResponse(Collections
                                                                                                       .singletonList(
                                                                                                               new FakeOBOTerm()));
        assertThat(termsResponse.getBody().getNumberOfHits(), is(1L));
        assertThat(termsResponse.getBody().getResults().size(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullOntologyService() {
        createOBOController(
                null,
                searchService,
                searchableField,
                retrievalConfig,
                graphImageService,
                oboControllerValidationHelper,
                ontologyPagingConfig,
                ontologySpecifier,
                headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchService() {
        createOBOController(ontologyService,
                            null,
                            searchableField,
                            retrievalConfig,
                            graphImageService,
                            oboControllerValidationHelper,
                            ontologyPagingConfig,
                            ontologySpecifier,
                            headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchableField() {
        createOBOController(ontologyService,
                            searchService,
                            null,
                            retrievalConfig,
                            graphImageService,
                            oboControllerValidationHelper,
                            ontologyPagingConfig,
                            ontologySpecifier,
                            headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullRetrievalConfig() {
        createOBOController(ontologyService,
                            searchService,
                            searchableField,
                            null,
                            graphImageService,
                            oboControllerValidationHelper,
                            ontologyPagingConfig,
                            ontologySpecifier,
                            headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullGraphImageService() {
        createOBOController(ontologyService,
                            searchService,
                            searchableField,
                            retrievalConfig,
                            null,
                            oboControllerValidationHelper,
                            ontologyPagingConfig,
                            ontologySpecifier,
                            headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullValidationHelper() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService, null,
                            ontologyPagingConfig, ontologySpecifier, headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullPagingConfig() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                            oboControllerValidationHelper, null, ontologySpecifier, headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullOntologySpecifier() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                            oboControllerValidationHelper, ontologyPagingConfig, null, headersProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullHeaderProvider() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                            oboControllerValidationHelper, ontologyPagingConfig, ontologySpecifier, null);
    }

    private static class FakeOBOTerm extends OBOTerm {}

}
