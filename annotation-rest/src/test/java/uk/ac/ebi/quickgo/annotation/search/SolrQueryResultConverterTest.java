package uk.ac.ebi.quickgo.annotation.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverter;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 29/04/2016
 * Time: 16:37
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrQueryResultConverterTest {

    @Mock
    private DocumentObjectBinder mockBinder;

    @Mock
    private AnnotationDocConverter mockDocConverter;

    @Mock
    private SolrDocumentList mockResults;

    private List<AnnotationDocument> solrTermDocs;

    @Mock
    private AnnotationDocument mockAnnotationDocument;

    @Mock
    private uk.ac.ebi.quickgo.annotation.model.Annotation mockAnnotation;

    @Before
    public void setup(){
        solrTermDocs = new ArrayList<>();
        solrTermDocs.add(mockAnnotationDocument);
        when(mockBinder.getBeans(AnnotationDocument.class, mockResults)).thenReturn(solrTermDocs);
        when(mockDocConverter.convert(mockAnnotationDocument)).thenReturn(mockAnnotation);
        when(mockResults.size()).thenReturn(1);
    }

    @Test
    public void successfullyConvertOneResult(){
        SolrQueryResultConverter converter = new SolrQueryResultConverter(mockBinder, mockDocConverter);
        List<Annotation> domainObjs = converter.convertResults(mockResults);
        assertThat(domainObjs, hasSize(1));
    }

    @Test
    public void successfullyConvertMultipleResults(){
        //Add another result
        solrTermDocs.add(mockAnnotationDocument);
        when(mockResults.size()).thenReturn(2);

        SolrQueryResultConverter converter = new SolrQueryResultConverter(mockBinder, mockDocConverter);
        List<Annotation> domainObjs = converter.convertResults(mockResults);
        assertThat(domainObjs, hasSize(2));
    }

}
