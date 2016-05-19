package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 12/05/2016
 * Time: 13:29
 * Created with IntelliJ IDEA.
 */
public class SearchServiceImplTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Mock
    RequestRetrieval<Annotation> requestRetrieval;

    @Mock
    QueryRequest queryRequest;

    @Test
    public void errorIfConstructorIsPassedANull(){
        thrown.expect(IllegalArgumentException.class);
        new SearchServiceImpl(null);
    }

    @Test
    public void findByQueryCalled(){
        thrown.expect(IllegalArgumentException.class);
        SearchService searchService =new SearchServiceImpl(requestRetrieval);
        searchService.findByQuery(queryRequest);
        verify(requestRetrieval.findByQuery(queryRequest));
    }
}
