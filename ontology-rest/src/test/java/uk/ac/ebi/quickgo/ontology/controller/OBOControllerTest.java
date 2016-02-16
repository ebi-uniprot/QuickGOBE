package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ResourceNotFoundException;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

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
    public void getTermResponseSucceeds() {
        FakeOBOTerm term = new FakeOBOTerm();
        term.id = "termId1";
        ResponseEntity<FakeOBOTerm> termResponse = controller.getTermResponse("id1", Collections.singletonList(term));
        assertThat(termResponse, is(notNullValue()));
    }

    @Test(expected = RetrievalException.class)
    public void getTermResponseFailsWhenResponseIsNotOne() {
        FakeOBOTerm term1 = new FakeOBOTerm();
        term1.id = "termId1";
        FakeOBOTerm term2 = new FakeOBOTerm();
        term2.id = "termId1";

        controller.getTermResponse("id1", Arrays.asList(term1, term2));
    }

    @Test(expected = RetrievalException.class)
    public void getTermResponseFailsWhenResponseIsNull() {
        FakeOBOTerm term1 = new FakeOBOTerm();
        term1.id = "termId1";
        FakeOBOTerm term2 = new FakeOBOTerm();
        term2.id = "termId1";

        controller.getTermResponse("id1", null);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTermResponseFailsWhenResponseContainsZeroElements() {
        controller.getTermResponse("id1", Collections.emptyList());
    }

    private static class FakeOBOTerm extends OBOTerm {
        String id;
    }

}