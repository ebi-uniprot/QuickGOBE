package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;

import java.util.Map;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Created 06/04/16
 * @author Edd
 */
public class GeneProductSolrQueryResultConverterTest {
    private GeneProductSolrQueryResultConverter converter;

    @Mock
    private DocumentObjectBinder binderMock;

    @Mock
    private GeneProductDocConverter geneProductConverterMock;

    @Mock
    private Map<String, String> fieldNameMap;

    @Before
    public void setUp() throws Exception {
        converter = new GeneProductSolrQueryResultConverter(binderMock, geneProductConverterMock, fieldNameMap);
    }

    @Test(expected = AssertionError.class)
    public void nullResultsListThrowsAssertionError() throws Exception {
        converter.convertResults(null);
    }

    // todo: GeneProductSolrQueryResultConverter tests
}