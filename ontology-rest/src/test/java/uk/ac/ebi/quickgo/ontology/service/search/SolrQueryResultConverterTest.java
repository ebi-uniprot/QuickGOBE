package uk.ac.ebi.quickgo.ontology.service.search;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
 * Tests the {@link SolrQueryResultConverter} implementation.
 *
 * Created 09/02/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class SolrQueryResultConverterTest {
    private SolrQueryResultConverter converter;

    @Mock
    private DocumentObjectBinder binderMock;

    @Mock
    private GODocConverter goConverterMock;

    @Mock
    private ECODocConverter ecoConverterMock;

    @Mock
    private Map<String, String> fieldNameMap;

    @BeforeEach
    void setUp() {
        converter = new SolrQueryResultConverter(binderMock, goConverterMock, ecoConverterMock, fieldNameMap);
    }

    @Test
    void nullResultsListThrowsAssertionError() {
        assertThrows(AssertionError.class, () -> converter.convertResults(null));
    }

    @Test
    void emptySolrDocListReturnsEmptyResults() {
        SolrDocumentList solrDocList = new SolrDocumentList();
        List<OntologyDocument> docTerms = Collections.emptyList();

        mockSolrDocToOntologyDocConversion(solrDocList, docTerms);

        List<OBOTerm> results = converter.convertResults(solrDocList);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void goSolrDocumentIsConvertedIntoGoTerm() {
        String termId = "GO:0006915";
        String termOntologyType = "GO";
        OntologyDocument ontologyDoc = createOntologyDoc(termId, termOntologyType);

        SolrDocumentList solrDocList = new SolrDocumentList();
        solrDocList.add(createSolrDocumentForOntologyDoc(ontologyDoc));

        List<OntologyDocument> docTerms = Collections.singletonList(ontologyDoc);
        mockSolrDocToOntologyDocConversion(solrDocList, docTerms);

        GOTerm goTerm = createGoTerm(termId);
        mockOntologyDocToGoTermConversion(ontologyDoc, goTerm);

        List<OBOTerm> results = converter.convertResults(solrDocList);

        assertThat(results, hasSize(1));
        assertThat(results.get(0), is(goTerm));
    }

    @Test
    void ecoSolrDocumentIsConvertedIntoGoTerm() {
        String termId = "ECO:0000200";
        String termOntologyType = "ECO";
        OntologyDocument ontologyDoc = createOntologyDoc(termId, termOntologyType);

        SolrDocumentList solrDocList = new SolrDocumentList();
        solrDocList.add(createSolrDocumentForOntologyDoc(ontologyDoc));

        List<OntologyDocument> docTerms = Collections.singletonList(ontologyDoc);
        mockSolrDocToOntologyDocConversion(solrDocList, docTerms);

        ECOTerm ecoTerm = createEcoTerm(termId);
        mockOntologyDocToEcoTermConversion(ontologyDoc, ecoTerm);

        List<OBOTerm> results = converter.convertResults(solrDocList);

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