package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 17:24
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationSolrQueryConverterTest {

    @Mock
    private SolrQuery mockSolrQuery;

    @Mock
    private QueryRequest mockRequest;

    @Test
    public void justReturns(){
        AnnotationSolrQueryConverter converter = new AnnotationSolrQueryConverter("/handler");
        verifyNoMoreInteractions(mockRequest);
        verifyNoMoreInteractions(mockSolrQuery);
        converter.assignQuery(mockRequest, mockSolrQuery);


    }
}
