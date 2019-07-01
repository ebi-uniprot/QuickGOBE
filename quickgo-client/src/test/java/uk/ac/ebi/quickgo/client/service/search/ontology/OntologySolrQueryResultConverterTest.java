package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.ECOTerm;
import uk.ac.ebi.quickgo.client.model.ontology.GOTerm;
import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.converter.ontology.ECODocConverter;
import uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
 * Tests the {@link OntologySolrQueryResultConverter} implementation.
 *
 * Created 09/02/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class OntologySolrQueryResultConverterTest {
    private OntologySolrQueryResultConverter converter;

    @Mock
    private DocumentObjectBinder binderMock;

    @Mock
    private GODocConverter goConverterMock;

    @Mock
    private ECODocConverter ecoConverterMock;

    @Mock
    private Map<String, String> fieldNameMap;

    @Before
    public void setUp() throws Exception {
        converter = new OntologySolrQueryResultConverter(binderMock, goConverterMock, ecoConverterMock, fieldNameMap);
    }

    @Test(expected = AssertionError.class)
    public void nullResultsListThrowsAssertionError() throws Exception {
        converter.convertResults(null);
    }

    @Test
    public void emptySolrDocListReturnsEmptyResults() throws Exception {
        SolrDocumentList solrDocList = new SolrDocumentList();
        List<OntologyDocument> docTerms = Collections.emptyList();

        mockSolrDocToOntologyDocConversion(solrDocList, docTerms);

        List<OntologyTerm> results = converter.convertResults(solrDocList);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    public void goSolrDocumentIsConvertedIntoGoTerm() throws Exception {
        String termId = "GO:0006915";
        String termOntologyType = "GO";
        OntologyDocument ontologyDoc = createOntologyDoc(termId, termOntologyType);

        SolrDocumentList solrDocList = new SolrDocumentList();
        solrDocList.add(createSolrDocumentForOntologyDoc(ontologyDoc));

        List<OntologyDocument> docTerms = Collections.singletonList(ontologyDoc);
        mockSolrDocToOntologyDocConversion(solrDocList, docTerms);

        GOTerm goTerm = createGoTerm(termId);
        mockOntologyDocToGoTermConversion(ontologyDoc, goTerm);

        List<OntologyTerm> results = converter.convertResults(solrDocList);

        assertThat(results, hasSize(1));
        assertThat(results.get(0), is(goTerm));
    }

    @Test
    public void ecoSolrDocumentIsConvertedIntoGoTerm() throws Exception {
        String termId = "ECO:0000200";
        String termOntologyType = "ECO";
        OntologyDocument ontologyDoc = createOntologyDoc(termId, termOntologyType);

        SolrDocumentList solrDocList = new SolrDocumentList();
        solrDocList.add(createSolrDocumentForOntologyDoc(ontologyDoc));

        List<OntologyDocument> docTerms = Collections.singletonList(ontologyDoc);
        mockSolrDocToOntologyDocConversion(solrDocList, docTerms);

        ECOTerm ecoTerm = createEcoTerm(termId);
        mockOntologyDocToEcoTermConversion(ontologyDoc, ecoTerm);

        List<OntologyTerm> results = converter.convertResults(solrDocList);

        assertThat(results, hasSize(1));
        assertThat(results.get(0), is(ecoTerm));
    }

    private void mockSolrDocToOntologyDocConversion(SolrDocumentList solrDocList, List<OntologyDocument> termDocs) {
        when(binderMock.getBeans(OntologyDocument.class, solrDocList)).thenReturn(termDocs);
    }

    private void mockOntologyDocToGoTermConversion(OntologyDocument ontologyDocument, GOTerm goTerm) {
        when(goConverterMock.convert(ontologyDocument)).thenReturn(goTerm);
    }

    private void mockOntologyDocToEcoTermConversion(OntologyDocument ontologyDocument, ECOTerm ecoTerm) {
        when(ecoConverterMock.convert(ontologyDocument)).thenReturn(ecoTerm);
    }

    private SolrDocument createSolrDocumentForOntologyDoc(OntologyDocument ontologyDocument) {
        SolrDocument doc = new SolrDocument();
        doc.setField("id", ontologyDocument.id);
        doc.setField("ontologyType", ontologyDocument.ontologyType);
        return doc;
    }

    private OntologyDocument createOntologyDoc(String id, String ontologyType) {
        OntologyDocument term = new OntologyDocument();
        term.id = id;
        term.ontologyType = ontologyType;

        return term;
    }

    private GOTerm createGoTerm(String id) {
        GOTerm goTerm = new GOTerm();
        goTerm.id = id;

        return goTerm;
    }

    private ECOTerm createEcoTerm(String id) {
        ECOTerm ecoTerm = new ECOTerm();
        ecoTerm.id = id;

        return ecoTerm;
    }
}