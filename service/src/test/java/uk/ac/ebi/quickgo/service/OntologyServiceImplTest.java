package uk.ac.ebi.quickgo.service;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyType;
import uk.ac.ebi.quickgo.repo.solr.io.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.service.converter.ontology.ECODocConverter;
import uk.ac.ebi.quickgo.service.converter.ontology.GODocConverter;
import uk.ac.ebi.quickgo.service.model.ontology.ECOTerm;
import uk.ac.ebi.quickgo.service.model.ontology.GOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocMocker.createECODoc;
import static uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocMocker.createGODoc;

/**
 * Testing the {@link OntologyServiceImpl} class.
 *
 * Created 11/11/15
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class OntologyServiceImplTest {

    private OntologyService<GOTerm> goOntologyService;
    private OntologyService<ECOTerm> ecoOntologyService;

    private OntologyRepository repositoryMock;
    private GODocConverter goDocumentConverterMock;
    private ECODocConverter ecoDocumentConverterMock;

    @Before
    public void setUp() throws Exception {
        repositoryMock = mock(OntologyRepository.class);
        goDocumentConverterMock = mock(GODocConverter.class);
        ecoDocumentConverterMock = mock(ECODocConverter.class);

        goOntologyService = new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, OntologyType.GO);
        ecoOntologyService = new OntologyServiceImpl<>(repositoryMock, ecoDocumentConverterMock, OntologyType.ECO);
    }

    @Test
    public void convertsEmptyOptionalDoc() {
        // create any OntologyServiceImpl to test its convertOptionalDoc method
        OntologyServiceImpl<GOTerm> ontologyServiceSpy =
                new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, OntologyType.GO);

        Optional<GOTerm> goTermOptional = ontologyServiceSpy.convertOptionalDoc(Optional.empty());
        assertThat(goTermOptional.isPresent(), is(false));
    }

    public class GOServiceTests {
        private GOTerm createGOTerm(String id) {
            GOTerm term = new GOTerm();
            term.id = id;
            return term;
        }

        @Test
        public void findsGoTermByIdentifier() throws Exception {
            String goId = "GO:0000001";

            OntologyDocument doc = createGODoc(goId, "name1");

            when(repositoryMock.findCompleteByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(goId)))
                    .thenReturn
                            (Optional.of(doc));

            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(goId));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findCompleteInfoByOntologyId(goId);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(goId)));
        }

        @Test
        public void findsAllGOTerms() {
            List<OntologyDocument> allDocs = new ArrayList<>();
            int realDocCount = 23;
            for (int i = 0; i < realDocCount; i++) {
                allDocs.add(createGODoc("id" + i, "name" + i));
            }
            Page<OntologyDocument> allDocsPage = new PageImpl<>(allDocs);

            when(repositoryMock.findAll(any(Pageable.class))).thenReturn(allDocsPage);

            when(goDocumentConverterMock.convert(any(OntologyDocument.class))).thenReturn(createGOTerm("stub"));

            int fakePageNumber = 1;
            int fakePageSize = 10;
            List<GOTerm> page = goOntologyService.findAll(new PageRequest(fakePageNumber, fakePageSize));
            assertThat(page.size(), is(realDocCount));
        }

        @Test
        public void findsEmptyOptionalForMissingGoIdentifier() {
            String ecoId = "GO:0000001";

            when(repositoryMock.findCompleteByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(ecoId)))
                    .thenReturn
                            (Optional.empty());

            Optional<ECOTerm> optionalEcoTerm = ecoOntologyService.findCompleteInfoByOntologyId(ecoId);
            assertThat(optionalEcoTerm.isPresent(), is(false));
        }

        @Test
        public void findsCoreInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock.findCoreByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id))).thenReturn
                    (Optional.of(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findCoreInfoByOntologyId(id);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsHistoryInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock.findHistoryByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findHistoryInfoByOntologyId(id);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXRefsInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock.findXRefsByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id))).thenReturn
                    (Optional.of(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findXRefsInfoByOntologyId(id);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsTaxonConstraintsInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock.findTaxonConstraintsByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findTaxonConstraintsInfoByOntologyId(id);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXOntologyRelationsInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findXOntologyRelationsByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findXORelationsInfoByOntologyId(id);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsAnnotationGuideLinesInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findAnnotationGuidelinesByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            Optional<GOTerm> optionalGoTerm = goOntologyService.findAnnotationGuideLinesInfoByOntologyId(id);
            assertThat(optionalGoTerm.isPresent(), is(true));

            GOTerm expectedGoTerm = optionalGoTerm.get();
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

    }

    public class ECOServiceTests {
        private ECOTerm createECOTerm(String id) {
            ECOTerm term = new ECOTerm();
            term.id = id;
            return term;
        }

        @Test
        public void findsEcoTermByIdentifier() {
            String ecoId = "ECO:0000001";

            OntologyDocument doc = createECODoc(ecoId, "name1");

            when(repositoryMock.findCompleteByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(ecoId)))
                    .thenReturn
                            (Optional.of(doc));

            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(ecoId));

            Optional<ECOTerm> optionalEcoTerm = ecoOntologyService.findCompleteInfoByOntologyId(ecoId);
            assertThat(optionalEcoTerm.isPresent(), is(true));

            ECOTerm expectedEcoTerm = optionalEcoTerm.get();
            assertThat(expectedEcoTerm.id, is(equalTo(ecoId)));
        }

        @Test
        public void findsEmptyOptionalForMissingEcoIdentifier() {
            String ecoId = "ECO:0000001";

            when(repositoryMock.findCompleteByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(ecoId)))
                    .thenReturn
                            (Optional.empty());

            Optional<ECOTerm> optionalEcoTerm = ecoOntologyService.findCompleteInfoByOntologyId(ecoId);
            assertThat(optionalEcoTerm.isPresent(), is(false));
        }

        @Test
        public void findsCoreInfoForEcoIdentifier() {
            String ecoId = "ECO:0000001";

            OntologyDocument doc = createECODoc(ecoId, "name1");

            when(repositoryMock.findCoreByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(ecoId)))
                    .thenReturn
                            (Optional.of(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(ecoId));

            Optional<ECOTerm> optionalEcoTerm = ecoOntologyService.findCoreInfoByOntologyId(ecoId);
            assertThat(optionalEcoTerm.isPresent(), is(true));

            ECOTerm expectedEcoTerm = optionalEcoTerm.get();
            assertThat(expectedEcoTerm.id, is(equalTo(ecoId)));
        }

        @Test
        public void findsHistoryInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock.findHistoryByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            Optional<ECOTerm> optionalTerm = ecoOntologyService.findHistoryInfoByOntologyId(id);
            assertThat(optionalTerm.isPresent(), is(true));

            ECOTerm expectedTerm = optionalTerm.get();
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXRefsInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock.findXRefsByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(id))).thenReturn
                    (Optional.of(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            Optional<ECOTerm> optionalTerm = ecoOntologyService.findXRefsInfoByOntologyId(id);
            assertThat(optionalTerm.isPresent(), is(true));

            ECOTerm expectedTerm = optionalTerm.get();
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsTaxonConstraintsInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock.findTaxonConstraintsByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            Optional<ECOTerm> optionalTerm = ecoOntologyService.findTaxonConstraintsInfoByOntologyId(id);
            assertThat(optionalTerm.isPresent(), is(true));

            ECOTerm expectedTerm = optionalTerm.get();
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXOntologyRelationsInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findXOntologyRelationsByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            Optional<ECOTerm> optionalTerm = ecoOntologyService.findXORelationsInfoByOntologyId(id);
            assertThat(optionalTerm.isPresent(), is(true));

            ECOTerm expectedTerm = optionalTerm.get();
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsAnnotationGuideLinesInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findAnnotationGuidelinesByTermId(OntologyType.ECO.name(), ClientUtils.escapeQueryChars(id)))
                    .thenReturn
                            (Optional.of(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            Optional<ECOTerm> optionalTerm = ecoOntologyService.findAnnotationGuideLinesInfoByOntologyId(id);
            assertThat(optionalTerm.isPresent(), is(true));

            ECOTerm expectedTerm = optionalTerm.get();
            assertThat(expectedTerm.id, is(equalTo(id)));
        }
    }
}