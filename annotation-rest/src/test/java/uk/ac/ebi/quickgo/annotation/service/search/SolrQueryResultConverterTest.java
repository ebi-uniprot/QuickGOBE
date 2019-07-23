package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.converter.AnnotationDocConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Mock
    private SearchServiceConfig.AnnotationCompositeRetrievalConfig mockAnnotationCompositeRetrievalConfig;

    private List<AnnotationDocument> solrTermDocs;

    private AnnotationDocument mockAnnotationDocument;

    @Mock
    private Annotation mockAnnotation;

    SolrQueryResultConverter converter;

    @Before
    public void setup(){
        mockAnnotationDocument = new AnnotationDocument();
        mockResults = new SolrDocumentList();
        solrTermDocs = new ArrayList<>();
        when(mockBinder.getBeans(AnnotationDocument.class, mockResults)).thenReturn(solrTermDocs);
        when(mockDocConverter.convert(mockAnnotationDocument)).thenReturn(mockAnnotation);
        converter = new SolrQueryResultConverter(mockBinder, mockDocConverter, mockAnnotationCompositeRetrievalConfig);


    }

    @Test
    public void successfullyConvertOneResult(){
        mockResults.add(new SolrDocument());
        solrTermDocs.add(mockAnnotationDocument);
        List<Annotation> domainObjs = converter.convertResults(mockResults);
        assertThat(domainObjs, hasSize(1));
    }

    @Test
    public void successfullyConvertMultipleResults(){
        //Add another result
        solrTermDocs.add(mockAnnotationDocument);
        solrTermDocs.add(mockAnnotationDocument);
        mockResults.addAll(Arrays.asList(new SolrDocument(), new SolrDocument()));

        SolrQueryResultConverter converter = new SolrQueryResultConverter(mockBinder, mockDocConverter,
                mockAnnotationCompositeRetrievalConfig);
        List<Annotation> domainObjs = converter.convertResults(mockResults);
        assertThat(domainObjs, hasSize(2));
    }
    @Test

    public void EmptyList(){
        List<Annotation> domainObjs = converter.convertResults(new SolrDocumentList());
        assertThat(domainObjs, hasSize(0));
    }
}
