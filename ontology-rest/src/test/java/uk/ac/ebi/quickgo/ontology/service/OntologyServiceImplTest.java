package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;
import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createECODoc;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createGODoc;

/**
 * Testing the {@link OntologyServiceImpl} class.
 *
 * Created 11/11/15
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class OntologyServiceImplTest {

    private OntologyServiceImpl<GOTerm> goOntologyService;
    private OntologyServiceImpl<ECOTerm> ecoOntologyService;

    private OntologyRepository repositoryMock;
    private GODocConverter goDocumentConverterMock;
    private ECODocConverter ecoDocumentConverterMock;

    @Before
    public void setUp() throws Exception {
        repositoryMock = mock(OntologyRepository.class);
        goDocumentConverterMock = mock(GODocConverter.class);
        ecoDocumentConverterMock = mock(ECODocConverter.class);

        goOntologyService = new OntologyServiceImpl<>
                (repositoryMock, goDocumentConverterMock, OntologyType.GO, new SolrQueryStringSanitizer());
        ecoOntologyService = new OntologyServiceImpl<>
                (repositoryMock, ecoDocumentConverterMock, OntologyType.ECO, new SolrQueryStringSanitizer());
    }

    @Test
    public void convertsEmptyDocList() {
        // create any OntologyServiceImpl to test its document conversion method
        OntologyServiceImpl<GOTerm> ontologyServiceSpy =
                new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, OntologyType.GO,
                        new SolrQueryStringSanitizer());

        List<GOTerm> goTerms = ontologyServiceSpy.convertDocs(Collections.emptyList());
        assertThat(goTerms.size(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRepoProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(null, goDocumentConverterMock, OntologyType.GO, new SolrQueryStringSanitizer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConverterProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, null, OntologyType.GO, new SolrQueryStringSanitizer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDocTypeProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, null,
                new SolrQueryStringSanitizer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullQueryStringSanitizerProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, OntologyType.GO, null);
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

            when(repositoryMock.findCompleteByTermId(OntologyType.GO.name(), idsViaOntologyService(goId)))
                    .thenReturn(singletonList(doc));

            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(goId));

            List<GOTerm> goTerms =
                    goOntologyService.findCompleteInfoByOntologyId(singletonList(goId));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(goId)));
        }

        @Test
        public void retrievesFirstPageOfGOTerms() {
            int fakePageNumber = 0;
            int fakePageSize = 10;

            List<OntologyDocument> allDocs = new ArrayList<>();

            long realDocCount = 23;

            for (int i = 0; i < realDocCount; i++) {
                allDocs.add(createGODoc("id" + i, "name" + i));
            }

            Pageable firstPageable = new PageRequest(fakePageNumber, fakePageSize);

            List<OntologyDocument> firstResultSet = allDocs.subList(0, fakePageSize);
            Page<OntologyDocument> firstPage =
                    new PageImpl<>(firstResultSet, firstPageable, realDocCount);

            when(repositoryMock.findAll(firstPageable)).thenReturn(firstPage);

            when(goDocumentConverterMock.convert(any(OntologyDocument.class))).thenReturn(createGOTerm("stub"));

            uk.ac.ebi.quickgo.rest.search.query.Page page =
                    new uk.ac.ebi.quickgo.rest.search.query.Page(fakePageNumber, fakePageSize);
            QueryResult<GOTerm> queryResult = goOntologyService.findAll(page);

            assertThat(queryResult.getResults(), hasSize(10));
            assertThat(queryResult.getPageInfo().getCurrent(), is(0));
            assertThat(queryResult.getPageInfo().getTotal(), is(3));
        }

        @Test
        public void findsEmptyListForMissingGoIdentifier() {
            String ecoId = "GO:0000001";

            when(repositoryMock
                    .findCompleteByTermId(OntologyType.ECO.name(), idsViaOntologyService(ecoId)))
                    .thenReturn(Collections.emptyList());

            List<ECOTerm> ecoTerms =
                    ecoOntologyService.findCompleteInfoByOntologyId(singletonList(ecoId));
            assertThat(ecoTerms.size(), is(0));
        }

        @Test
        public void findsCoreInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            List<GOTerm> goTerms = goOntologyService.findCoreInfoByOntologyId(singletonList(id));
            assertThat(goTerms, is(not(nullValue())));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsHistoryInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findHistoryByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            List<GOTerm> goTerms = goOntologyService.findHistoryInfoByOntologyId(singletonList(id));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXRefsInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findXRefsByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            List<GOTerm> goTerms =
                    goOntologyService.findXRefsInfoByOntologyId(singletonList(id));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsTaxonConstraintsInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findTaxonConstraintsByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            List<GOTerm> goTerms = goOntologyService.findTaxonConstraintsInfoByOntologyId(singletonList(id));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXOntologyRelationsInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findXOntologyRelationsByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            List<GOTerm> goTerms = goOntologyService.findXORelationsInfoByOntologyId(singletonList(id));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsAnnotationGuideLinesInfoForGoIdentifier() {
            String id = "GO:0000001";

            OntologyDocument doc = createGODoc(id, "name1");

            when(repositoryMock
                    .findAnnotationGuidelinesByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(createGOTerm(id));

            List<GOTerm> goTerms = goOntologyService.findAnnotationGuideLinesInfoByOntologyId(singletonList(id));
            assertThat(goTerms.size(), is(1));

            GOTerm expectedGoTerm = goTerms.get(0);
            assertThat(expectedGoTerm.id, is(equalTo(id)));
        }

        private List<String> idsViaOntologyService(String... ids) {
            return goOntologyService.buildIdList(Arrays.asList(ids));
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

            when(repositoryMock
                    .findCompleteByTermId(OntologyType.ECO.name(), idsViaOntologyService(ecoId)))
                    .thenReturn(singletonList(doc));

            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(ecoId));

            List<ECOTerm> ecoTerms = ecoOntologyService.findCompleteInfoByOntologyId(singletonList(ecoId));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedEcoTerm = ecoTerms.get(0);
            assertThat(expectedEcoTerm.id, is(equalTo(ecoId)));
        }

        @Test
        public void findsEmptyListForMissingEcoIdentifier() {
            String ecoId = "ECO:0000001";

            when(repositoryMock
                    .findCompleteByTermId(OntologyType.ECO.name(), idsViaOntologyService(ecoId)))
                    .thenReturn(Collections.emptyList());

            List<ECOTerm> ecoTerms = ecoOntologyService.findCompleteInfoByOntologyId(singletonList(ecoId));
            assertThat(ecoTerms.size(), is(0));
        }

        @Test
        public void findsCoreInfoForEcoIdentifier() {
            String ecoId = "ECO:0000001";

            OntologyDocument doc = createECODoc(ecoId, "name1");

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.ECO.name(), idsViaOntologyService(ecoId)))
                    .thenReturn(singletonList((doc)));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(ecoId));

            List<ECOTerm> ecoTerms = ecoOntologyService.findCoreInfoByOntologyId(singletonList(ecoId));
            assertThat(ecoTerms, is(not(nullValue())));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedEcoTerm = ecoTerms.get(0);
            assertThat(expectedEcoTerm.id, is(equalTo(ecoId)));
        }

        @Test
        public void findsHistoryInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findHistoryByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            List<ECOTerm> ecoTerms = ecoOntologyService.findHistoryInfoByOntologyId(singletonList(id));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedTerm = ecoTerms.get(0);
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXRefsInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findXRefsByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            List<ECOTerm> ecoTerms =
                    ecoOntologyService.findXRefsInfoByOntologyId(singletonList(id));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedTerm = ecoTerms.get(0);
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsTaxonConstraintsInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findTaxonConstraintsByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            List<ECOTerm> ecoTerms = ecoOntologyService.findTaxonConstraintsInfoByOntologyId(singletonList(id));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedTerm = ecoTerms.get(0);
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsXOntologyRelationsInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findXOntologyRelationsByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            List<ECOTerm> ecoTerms = ecoOntologyService.findXORelationsInfoByOntologyId(singletonList(id));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedTerm = ecoTerms.get(0);
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        @Test
        public void findsAnnotationGuideLinesInfoForEcoIdentifier() {
            String id = "ECO:0000001";

            OntologyDocument doc = createECODoc(id, "name1");

            when(repositoryMock
                    .findAnnotationGuidelinesByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(createECOTerm(id));

            List<ECOTerm> ecoTerms = ecoOntologyService.findAnnotationGuideLinesInfoByOntologyId(singletonList(id));
            assertThat(ecoTerms.size(), is(1));

            ECOTerm expectedTerm = ecoTerms.get(0);
            assertThat(expectedTerm.id, is(equalTo(id)));
        }

        private List<String> idsViaOntologyService(String... ids) {
            return ecoOntologyService.buildIdList(Arrays.asList(ids));
        }
    }
}