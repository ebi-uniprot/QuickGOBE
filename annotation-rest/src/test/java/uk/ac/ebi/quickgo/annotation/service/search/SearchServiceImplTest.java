package uk.ac.ebi.quickgo.annotation.service.search;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.RequestRetrieval;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

/**
 * @author Tony Wardell
 * Date: 12/05/2016
 * Time: 13:29
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    RequestRetrieval<Annotation> requestRetrieval;

    @Mock
    QueryRequest queryRequest;

    @Test
    void errorIfConstructorIsPassedANull(){
        assertThrows(IllegalArgumentException.class, () -> new SearchServiceImpl(null));
    }

    @Test
    void findByQueryCalled(){
        SearchService<Annotation> searchService = new SearchServiceImpl(requestRetrieval);
        searchService.findByQuery(queryRequest);
        verify(requestRetrieval, atLeastOnce()).findByQuery(queryRequest);
    }
}
