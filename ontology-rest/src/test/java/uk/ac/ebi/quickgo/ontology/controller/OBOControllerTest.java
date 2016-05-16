package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Collections;
import java.util.function.Predicate;
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

    @Mock
    SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig;
    @Mock
    private OntologyService<FakeOBOTerm> ontologyService;
    @Mock
    private SearchService<OBOTerm> searchService;
    @Mock
    private SearchableField searchableField;

    private OBOController<FakeOBOTerm> controller;

    @Before
    public void setUp() {
        this.controller = createOBOController(ontologyService, searchService, searchableField, retrievalConfig);
    }

    @Test
    public void termsResponseForNullListContainsZeroResults() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getTermsResponse(null);
        assertThat(termsResponse.getBody().getNumberOfHits(), is(0L));
        assertThat(termsResponse.getBody().getResults(), is(empty()));
    }

    @Test
    public void termsResponseForEmptyListContainsZeroResults() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getTermsResponse(Collections.emptyList());
        assertThat(termsResponse.getBody().getNumberOfHits(), is(0L));
        assertThat(termsResponse.getBody().getResults(), is(empty()));
    }

    @Test
    public void termsResponseForListOfOneContainsOneResult() {
        ResponseEntity<QueryResult<FakeOBOTerm>> termsResponse = controller.getTermsResponse(Collections
                .singletonList(new FakeOBOTerm()));
        assertThat(termsResponse.getBody().getNumberOfHits(), is(1L));
        assertThat(termsResponse.getBody().getResults().size(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullOntologyService() {
        createOBOController(
                null, searchService, searchableField, retrievalConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchService() {
        createOBOController(ontologyService, null, searchableField, retrievalConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchableField() {
        createOBOController(ontologyService, searchService, null, retrievalConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullRetrievalConfig() {
        createOBOController(ontologyService, searchService, searchableField, null);
    }

    private static OBOController<FakeOBOTerm> createOBOController(
            final OntologyService<FakeOBOTerm> ontologyService, final SearchService<OBOTerm> searchService,
            final SearchableField searchableField,
            final SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig) {
        return new OBOController<FakeOBOTerm>(ontologyService, searchService, searchableField, retrievalConfig) {
            @Override protected Predicate<String> idValidator() {
                return id -> ID_FORMAT.matcher(id).matches();
            }

            @Override protected OntologyType getOntologyType() {
                return OntologyType.GO;
            }
        };
    }

    private static class FakeOBOTerm extends OBOTerm {}

}