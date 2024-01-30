package uk.ac.ebi.quickgo.geneproduct.service.search;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * * Tests the {@link GeneProductSolrQueryResultConverter} implementation.
 *
 * Created 06/04/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class GeneProductSolrQueryResultConverterTest {
    private GeneProductSolrQueryResultConverter converter;

    @Mock
    private DocumentObjectBinder binderMock;

    @Mock
    private GeneProductDocConverter geneProductConverterMock;

    @BeforeEach
    void setUp() throws Exception {
        converter = new GeneProductSolrQueryResultConverter(binderMock, geneProductConverterMock, new HashMap<>());
    }

    @Test
    void nullResultsListThrowsAssertionError() throws Exception {
        assertThrows(AssertionError.class, () -> converter.convertResults(null));
    }

    @Test
    void emptySolrDocListReturnsEmptyResults() throws Exception {
        SolrDocumentList solrDocList = new SolrDocumentList();
        List<GeneProductDocument> docTerms = Collections.emptyList();

        mockSolrDocToGeneProductDocConversion(solrDocList, docTerms);

        List<GeneProduct> results = converter.convertResults(solrDocList);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void geneProductSolrDocumentIsConvertedIntoGeneProduct() throws Exception {
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