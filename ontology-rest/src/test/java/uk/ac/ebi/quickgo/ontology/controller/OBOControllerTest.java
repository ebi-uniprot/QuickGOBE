package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyRestConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Collections;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
    private OBOController<FakeOBOTerm> controller;

    private static OBOController<FakeOBOTerm> createOBOController(
            final OntologyService<FakeOBOTerm> ontologyService, final SearchService<OBOTerm> searchService,
            final SearchableField searchableField,
            final SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig,
            final GraphImageService graphImageService,
            OBOControllerValidationHelper oboControllerValidationHelper,
            OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig,
            OntologyType ontologyType) {
        return new OBOController<FakeOBOTerm>(ontologyService,
                                              searchService,
                                              searchableField,
                                              retrievalConfig,
                                              graphImageService,
                                              oboControllerValidationHelper,
                                              ontologyPagingConfig,
                                              ontologyType) {};
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
                                              OntologyType.GO);
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
                OntologyType.GO);
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
                            OntologyType.GO);
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
                            OntologyType.GO);
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
                            OntologyType.GO);
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
                            OntologyType.GO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullValidationHelper() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService, null,
                            ontologyPagingConfig, OntologyType.GO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullPagingConfig() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                            oboControllerValidationHelper, null, OntologyType.GO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullOntologyType() {
        createOBOController(ontologyService, searchService, searchableField, retrievalConfig, graphImageService,
                            oboControllerValidationHelper, ontologyPagingConfig, null);
    }

    private static class FakeOBOTerm extends OBOTerm {}

}
