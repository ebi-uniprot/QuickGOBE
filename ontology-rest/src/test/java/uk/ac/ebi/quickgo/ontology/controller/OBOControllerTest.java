package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
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
    private OBOController<FakeOBOTerm> controller;
    private static final Pattern ID_FORMAT = Pattern.compile("id[0-9]");

    @Mock
    private OntologyService<FakeOBOTerm> ontologyService;
    @Mock
    private SearchService<OBOTerm> searchService;
    @Mock
    private SearchableField searchableField;
    @Mock
    SearchServiceConfig.OntologyCompositeRetrievalConfig retrievalConfig;

    @Before

    public void setUp() {
        this.controller =
                new OBOController<FakeOBOTerm>(ontologyService, searchService, searchableField, retrievalConfig) {
                    @Override protected boolean isValidId(String id) {
                        return ID_FORMAT.matcher(id).matches();
                    }

                    @Override protected OntologyType getOntologyType() {
                        return OntologyType.GO;
                    }
                };
    }

    @Test
    public void checkValidId() {
        controller.checkValidId("id0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkInvalidId() {
        controller.checkValidId("wrongIdFormat");
    }

    @Test
    public void validatesValidRequestedResults() {
        controller.validateRequestedResults(OBOController.MAX_PAGE_RESULTS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validatesInvalidRequestedResults() {
        controller.validateRequestedResults(OBOController.MAX_PAGE_RESULTS + 1);
    }

    @Test
    public void createsListFromNullCSV() {
        assertThat(controller.csvToList(null).size(), is(0));
    }

    @Test
    public void createsListFromCSVForNoItems() {
        assertThat(controller.csvToList("").size(), is(0));
    }

    @Test
    public void createsListFromCSVForOneItem() {
        assertThat(controller.csvToList("a").size(), is(1));
    }

    @Test
    public void createsListFromCSVForTwoItems() {
        assertThat(controller.csvToList("a,b").size(), is(2));
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

    private static class FakeOBOTerm extends OBOTerm {}

}