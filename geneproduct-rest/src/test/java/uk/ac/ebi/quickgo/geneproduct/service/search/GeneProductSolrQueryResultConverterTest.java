package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;

import java.util.Collections;
import java.util.HashMap;
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
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * * Tests the {@link GeneProductSolrQueryResultConverter} implementation.
 *
 * Created 06/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneProductSolrQueryResultConverterTest {
    private GeneProductSolrQueryResultConverter converter;

    @Mock
    private DocumentObjectBinder binderMock;

    @Mock
    private GeneProductDocConverter geneProductConverterMock;

    @Before
    public void setUp() throws Exception {
        converter = new GeneProductSolrQueryResultConverter(binderMock, geneProductConverterMock, new HashMap<>());
    }

    @Test(expected = AssertionError.class)
    public void nullResultsListThrowsAssertionError() throws Exception {
        converter.convertResults(null);
    }

    @Test
    public void emptySolrDocListReturnsEmptyResults() throws Exception {
        SolrDocumentList solrDocList = new SolrDocumentList();
        List<GeneProductDocument> docTerms = Collections.emptyList();

        mockSolrDocToGeneProductDocConversion(solrDocList, docTerms);

        List<GeneProduct> results = converter.convertResults(solrDocList);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    public void geneProductSolrDocumentIsConvertedIntoGeneProduct() throws Exception {
        String termId = "A0A015KZI4";
        GeneProductDocument geneProductDoc = createGeneProductDoc(termId);

        SolrDocumentList solrDocList = new SolrDocumentList();
        solrDocList.add(createSolrDocumentForGeneProductDoc(geneProductDoc));

        List<GeneProductDocument> docTerms = Collections.singletonList(geneProductDoc);
        mockSolrDocToGeneProductDocConversion(solrDocList, docTerms);

        GeneProduct goTerm = createGeneProduct(termId);
        mockGeneProductDocToGeneProductConversion(geneProductDoc, goTerm);

        List<GeneProduct> results = converter.convertResults(solrDocList);

        assertThat(results, hasSize(1));
        assertThat(results.get(0), is(goTerm));
    }

    private void mockSolrDocToGeneProductDocConversion(SolrDocumentList solrDocList, List<GeneProductDocument> termDocs) {
        when(binderMock.getBeans(GeneProductDocument.class, solrDocList)).thenReturn(termDocs);
    }

    private SolrDocument createSolrDocumentForGeneProductDoc(GeneProductDocument geneProductDocument) {
        SolrDocument doc = new SolrDocument();
        doc.setField("identifier", geneProductDocument.id);
        return doc;
    }

    private GeneProductDocument createGeneProductDoc(String id) {
        GeneProductDocument term = new GeneProductDocument();
        term.id = id;

        return term;
    }

    private void mockGeneProductDocToGeneProductConversion(GeneProductDocument geneProductDocument, GeneProduct geneProduct) {
        when(geneProductConverterMock.convert(geneProductDocument)).thenReturn(geneProduct);
    }

    private GeneProduct createGeneProduct(String id) {
        GeneProduct geneProduct = new GeneProduct();
        geneProduct.id = id;

        return geneProduct;
    }
}