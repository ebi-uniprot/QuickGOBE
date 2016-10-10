package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTerm;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermRepository;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermSource;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.service.converter.ECODocConverter;
import uk.ac.ebi.quickgo.ontology.service.converter.GODocConverter;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraphTraversal;
import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
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
    private OntologyGraphTraversal ontologyTraversalMock;
    private CoTermRepository coTermsRepositoryMock;

    @Before
    public void setUp() throws Exception {
        repositoryMock = mock(OntologyRepository.class);
        goDocumentConverterMock = mock(GODocConverter.class);
        ecoDocumentConverterMock = mock(ECODocConverter.class);
        ontologyTraversalMock = mock(OntologyGraphTraversal.class);
        coTermsRepositoryMock = mock(CoTermRepository.class);

        goOntologyService = new OntologyServiceImpl<>
                (repositoryMock,
                        goDocumentConverterMock,
                        OntologyType.GO,
                        new SolrQueryStringSanitizer(),
                        ontologyTraversalMock,
                        coTermsRepositoryMock);
        ecoOntologyService = new OntologyServiceImpl<>
                (repositoryMock,
                        ecoDocumentConverterMock,
                        OntologyType.ECO,
                        new SolrQueryStringSanitizer(),
                        ontologyTraversalMock,
                        coTermsRepositoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRepoProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(null, goDocumentConverterMock, OntologyType.GO, new SolrQueryStringSanitizer(),
                ontologyTraversalMock, coTermsRepositoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConverterProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, null, OntologyType.GO, new SolrQueryStringSanitizer(),
                ontologyTraversalMock, coTermsRepositoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDocTypeProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, null,
                new SolrQueryStringSanitizer(), ontologyTraversalMock, coTermsRepositoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullQueryStringSanitizerProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, OntologyType.GO, null,
                ontologyTraversalMock, coTermsRepositoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOntologyTraversalProducesIllegalArgumentException() {
        new OntologyServiceImpl<>(repositoryMock, goDocumentConverterMock, OntologyType.GO, new
                SolrQueryStringSanitizer(), null, coTermsRepositoryMock);
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
            int zeroBasedPageNumber = 0;
            int oneBasedPageNumber = zeroBasedPageNumber + 1;

            int pageSize = 10;

            List<OntologyDocument> allDocs = new ArrayList<>();

            long realDocCount = 23;

            for (int i = 0; i < realDocCount; i++) {
                allDocs.add(createGODoc("id" + i, "name" + i));
            }

            Pageable firstPageable = new PageRequest(zeroBasedPageNumber, pageSize);

            List<OntologyDocument> firstResultSet = allDocs.subList(0, pageSize);
            Page<OntologyDocument> firstPage =
                    new PageImpl<>(firstResultSet, firstPageable, realDocCount);

            when(repositoryMock.findAllByOntologyType(OntologyType.GO.name(), firstPageable)).thenReturn(firstPage);

            when(goDocumentConverterMock.convert(any(OntologyDocument.class))).thenReturn(createGOTerm("stub"));

            uk.ac.ebi.quickgo.rest.search.query.Page page =
                    new uk.ac.ebi.quickgo.rest.search.query.Page(oneBasedPageNumber, pageSize);
            QueryResult<GOTerm> queryResult = goOntologyService.findAllByOntologyType(OntologyType.GO, page);

            assertThat(queryResult.getResults(), hasSize(10));
            assertThat(queryResult.getPageInfo().getCurrent(), is(1));
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

        @Test
        public void findsEmptyAncestorsForMissingTerm() {
            Set<String> ids = idsViaOntologyService("GO:0000001").stream().collect(Collectors.toSet());
            when(ontologyTraversalMock.ancestors(ids)).thenReturn(Collections.emptyList());
            List<GOTerm> ancestors = goOntologyService.findAncestorsInfoByOntologyId(asList(ids));
            assertThat(ancestors.size(), is(0));
        }

        @Test
        public void findsAncestorsForTerm() {
            String id = "GO:0000001";
            List<String> myAncestors = singletonList("GO:0000002");

            OntologyDocument doc = createGODoc(id, "name1");
            GOTerm term = createGOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.ancestors(singleton(id))).thenReturn(myAncestors);

            List<GOTerm> ancestors = goOntologyService.findAncestorsInfoByOntologyId(singletonList(id));

            assertThat(ancestors.size(), is(1));
            assertThat(ancestors.get(0).ancestors, is(myAncestors));
        }

        @Test
        public void findsAncestorsForTermWithRelation() {
            String id = "GO:0000001";
            List<String> myAncestors = singletonList("GO:0000002");

            OntologyDocument doc = createGODoc(id, "name1");
            GOTerm term = createGOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.ancestors(singleton(id), OntologyRelationType.CAPABLE_OF))
                    .thenReturn(myAncestors);

            List<GOTerm> ancestors =
                    goOntologyService.findAncestorsInfoByOntologyId(singletonList(id), OntologyRelationType.CAPABLE_OF);

            assertThat(ancestors.size(), is(1));
            assertThat(ancestors.get(0).ancestors, is(myAncestors));
        }

        @Test
        public void findsEmptyDescendantsForMissingTerm() {
            Set<String> ids = idsViaOntologyService("GO:0000001").stream().collect(Collectors.toSet());
            when(ontologyTraversalMock.descendants(ids)).thenReturn(Collections.emptyList());
            List<GOTerm> descendants = goOntologyService.findDescendantsInfoByOntologyId(asList(ids));
            assertThat(descendants.size(), is(0));
        }

        @Test
        public void findsDescendantsForTerm() {
            String id = "GO:0000001";
            List<String> myDescendants = singletonList("GO:0000002");

            OntologyDocument doc = createGODoc(id, "name1");
            GOTerm term = createGOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.descendants(singleton(id)))
                    .thenReturn(myDescendants);

            List<GOTerm> descendants = goOntologyService
                    .findDescendantsInfoByOntologyId(singletonList(id));

            assertThat(descendants.size(), is(1));
            assertThat(descendants.get(0).descendants, is(myDescendants));
        }

        @Test
        public void findsDescendantsForTermWithRelation() {
            String id = "GO:0000001";
            List<String> myDescendants = singletonList("GO:0000002");

            OntologyDocument doc = createGODoc(id, "name1");
            GOTerm term = createGOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.GO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(goDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.descendants(singleton(id), OntologyRelationType.CAPABLE_OF))
                    .thenReturn(myDescendants);

            List<GOTerm> descendants = goOntologyService
                    .findDescendantsInfoByOntologyId(singletonList(id), OntologyRelationType.CAPABLE_OF);

            assertThat(descendants.size(), is(1));
            assertThat(descendants.get(0).descendants, is(myDescendants));
        }

        @Test(expected = IllegalArgumentException.class)
        public void illegalArgumentWhenFindingPathsForZeroFromTerms() {
            Set<String> fromIds = Collections.emptySet();
            Set<String> toIds = idsViaOntologyService("GO:0000001").stream().collect(Collectors.toSet());
            doThrow(IllegalArgumentException.class).when(ontologyTraversalMock).paths(fromIds, toIds);
            goOntologyService.paths(fromIds, toIds);
        }

        @Test(expected = IllegalArgumentException.class)
        public void illegalArgumentWhenFindingPathsForZeroToTerms() {
            Set<String> fromIds = idsViaOntologyService("GO:0000001").stream().collect(Collectors.toSet());
            Set<String> toIds = Collections.emptySet();
            doThrow(IllegalArgumentException.class).when(ontologyTraversalMock).paths(fromIds, toIds);
            goOntologyService.paths(fromIds, toIds);
        }

        @Test
        public void findsEmptyPathsForMissingTerm() {
            Set<String> fromIds = idsViaOntologyService("GO:0000001").stream().collect(Collectors.toSet());
            Set<String> toIds = idsViaOntologyService("GO:0000002").stream().collect(Collectors.toSet());

            when(ontologyTraversalMock.paths(fromIds, toIds)).thenReturn(Collections.emptyList());
            List<List<OntologyRelationship>> paths = goOntologyService.paths(fromIds, toIds);

            assertThat(paths.size(), is(0));
        }

        @Test
        public void findsPathsForTerm() {
            String child = "GO:0000001";
            String parent = "GO:0000002";
            Set<String> fromIds = idsViaOntologyService(child).stream().collect(Collectors.toSet());
            Set<String> toIds = idsViaOntologyService(parent).stream().collect(Collectors.toSet());

            when(ontologyTraversalMock.paths(fromIds, toIds)).thenReturn(Collections.singletonList(Collections
                    .singletonList(new OntologyRelationship(child, parent, OntologyRelationType.IS_A))));
            List<List<OntologyRelationship>> paths = goOntologyService.paths(fromIds, toIds);

            assertThat(paths.size(), is(1));
        }

        @Test
        public void findsPathsForTermWithRelation() {
            String child = "GO:0000001";
            String parent = "GO:0000002";
            Set<String> fromIds = idsViaOntologyService(child).stream().collect(Collectors.toSet());
            Set<String> toIds = idsViaOntologyService(parent).stream().collect(Collectors.toSet());

            when(ontologyTraversalMock.paths(fromIds, toIds, OntologyRelationType.IS_A))
                    .thenReturn(Collections.singletonList(Collections
                            .singletonList(new OntologyRelationship(child, parent, OntologyRelationType.IS_A))));
            List<List<OntologyRelationship>> paths = goOntologyService.paths(fromIds, toIds, OntologyRelationType.IS_A);

            assertThat(paths.size(), is(1));
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

        @Test
        public void findsEmptyAncestorsForMissingTerm() {
            Set<String> ids = idsViaOntologyService("ECO:0000001").stream().collect(Collectors.toSet());
            when(ontologyTraversalMock.ancestors(ids)).thenReturn(Collections.emptyList());
            List<ECOTerm> ancestors = ecoOntologyService.findAncestorsInfoByOntologyId(asList(ids));
            assertThat(ancestors.size(), is(0));
        }

        @Test
        public void findsAncestorsForTerm() {
            String id = "ECO:0000001";
            List<String> myAncestors = singletonList("ECO:0000002");

            OntologyDocument doc = createECODoc(id, "name1");
            ECOTerm term = createECOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.ancestors(singleton(id))).thenReturn(myAncestors);

            List<ECOTerm> ancestors = ecoOntologyService.findAncestorsInfoByOntologyId(singletonList(id));

            assertThat(ancestors.size(), is(1));
            assertThat(ancestors.get(0).ancestors, is(myAncestors));
        }

        @Test
        public void findsAncestorsForTermWithRelation() {
            String id = "ECO:0000001";
            List<String> myAncestors = singletonList("ECO:0000002");

            OntologyDocument doc = createECODoc(id, "name1");
            ECOTerm term = createECOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.ancestors(singleton(id), OntologyRelationType.CAPABLE_OF))
                    .thenReturn(myAncestors);

            List<ECOTerm> ancestors = ecoOntologyService
                    .findAncestorsInfoByOntologyId(singletonList(id), OntologyRelationType.CAPABLE_OF);

            assertThat(ancestors.size(), is(1));
            assertThat(ancestors.get(0).ancestors, is(myAncestors));
        }

        @Test
        public void findsEmptyDescendantsForMissingTerm() {
            Set<String> ids = idsViaOntologyService("ECO:0000001").stream().collect(Collectors.toSet());
            when(ontologyTraversalMock.descendants(ids)).thenReturn(Collections.emptyList());
            List<ECOTerm> descendants = ecoOntologyService.findDescendantsInfoByOntologyId(asList(ids));
            assertThat(descendants.size(), is(0));
        }

        @Test
        public void findsDescendantsForTerm() {
            String id = "ECO:0000001";
            List<String> myDescendants = singletonList("ECO:0000002");

            OntologyDocument doc = createECODoc(id, "name1");
            ECOTerm term = createECOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.descendants(singleton(id))).thenReturn(myDescendants);

            List<ECOTerm> descendants = ecoOntologyService.findDescendantsInfoByOntologyId(singletonList(id));

            assertThat(descendants.size(), is(1));
            assertThat(descendants.get(0).descendants, is(myDescendants));
        }

        @Test
        public void findsDescendantsForTermWithRelation() {
            String id = "ECO:0000001";
            List<String> myDescendants = singletonList("ECO:0000002");

            OntologyDocument doc = createECODoc(id, "name1");
            ECOTerm term = createECOTerm(id);

            when(repositoryMock
                    .findCoreAttrByTermId(OntologyType.ECO.name(), idsViaOntologyService(id)))
                    .thenReturn(singletonList(doc));
            when(ecoDocumentConverterMock.convert(doc)).thenReturn(term);
            when(ontologyTraversalMock.descendants(singleton(id), OntologyRelationType.CAPABLE_OF))
                    .thenReturn(myDescendants);

            List<ECOTerm> descendants = ecoOntologyService.findDescendantsInfoByOntologyId(singletonList(id),
                    OntologyRelationType.CAPABLE_OF);

            assertThat(descendants.size(), is(1));
            assertThat(descendants.get(0).descendants, is(myDescendants));
        }

        @Test(expected = IllegalArgumentException.class)
        public void illegalArgumentWhenFindingPathsForZeroFromTerms() {
            Set<String> fromIds = Collections.emptySet();
            Set<String> toIds = idsViaOntologyService("ECO:0000001").stream().collect(Collectors.toSet());
            doThrow(IllegalArgumentException.class).when(ontologyTraversalMock).paths(fromIds, toIds);
            ecoOntologyService.paths(fromIds, toIds);
        }

        @Test(expected = IllegalArgumentException.class)
        public void illegalArgumentWhenFindingPathsForZeroToTerms() {
            Set<String> fromIds = idsViaOntologyService("ECO:0000001").stream().collect(Collectors.toSet());
            Set<String> toIds = Collections.emptySet();
            doThrow(IllegalArgumentException.class).when(ontologyTraversalMock).paths(fromIds, toIds);
            ecoOntologyService.paths(fromIds, toIds);
        }

        @Test
        public void findsEmptyPathsForMissingTerm() {
            Set<String> fromIds = idsViaOntologyService("ECO:0000001").stream().collect(Collectors.toSet());
            Set<String> toIds = idsViaOntologyService("ECO:0000002").stream().collect(Collectors.toSet());

            when(ontologyTraversalMock.paths(fromIds, toIds)).thenReturn(Collections.emptyList());
            List<List<OntologyRelationship>> paths = ecoOntologyService.paths(fromIds, toIds);

            assertThat(paths.size(), is(0));
        }

        @Test
        public void findsPathsForTerm() {
            String child = "ECO:0000001";
            String parent = "ECO:0000002";
            Set<String> fromIds = idsViaOntologyService(child).stream().collect(Collectors.toSet());
            Set<String> toIds = idsViaOntologyService(parent).stream().collect(Collectors.toSet());

            when(ontologyTraversalMock.paths(fromIds, toIds)).thenReturn(Collections.singletonList(Collections
                    .singletonList(new OntologyRelationship(child, parent, OntologyRelationType.IS_A))));
            List<List<OntologyRelationship>> paths = ecoOntologyService.paths(fromIds, toIds);

            assertThat(paths.size(), is(1));
        }

        @Test
        public void findsPathsForTermWithRelation() {
            String child = "ECO:0000001";
            String parent = "ECO:0000002";
            Set<String> fromIds = idsViaOntologyService(child).stream().collect(Collectors.toSet());
            Set<String> toIds = idsViaOntologyService(parent).stream().collect(Collectors.toSet());

            when(ontologyTraversalMock.paths(fromIds, toIds, OntologyRelationType.IS_A))
                    .thenReturn(Collections.singletonList(Collections
                            .singletonList(new OntologyRelationship(child, parent, OntologyRelationType.IS_A))));
            List<List<OntologyRelationship>> paths =
                    ecoOntologyService.paths(fromIds, toIds, OntologyRelationType.IS_A);

            assertThat(paths.size(), is(1));
        }

        private List<String> idsViaOntologyService(String... ids) {
            return ecoOntologyService.buildIdList(Arrays.asList(ids));
        }
    }

    public class CoTermServiceTests{
        @Test
        public void retrievesListOfCoTermsForGoTerm() {

            String id = "GO:0003824";
            int limit = 5;
            float simThreshold = 0f;

            CoTerm coTerm1 = new CoTerm("GO:0003824", "GO:0003824", 11.63f, 100f, 3948313, 3948313);
            CoTerm coTerm2 = new CoTerm("GO:0003824", "GO:0008152", 5.47f, 24.64f, 1346183, 2861162);
            CoTerm coTerm3 = new CoTerm("GO:0003824", "GO:0016740", 2.27f, 12.65f, 1043613, 5345589);

            List<CoTerm> results = Arrays.asList(coTerm1, coTerm2, coTerm3 );

            when(coTermsRepositoryMock.findCoTerms(id, CoTermSource.MANUAL, limit, simThreshold )).thenReturn(results);

            List<CoTerm> coTerms = goOntologyService.findCoTermsByOntologyId(id, CoTermSource.MANUAL, limit,
                    simThreshold );

            assertThat(coTerms, hasSize(3));
            assertThat(coTerms, containsInAnyOrder(coTerm1, coTerm2, coTerm3));
        }


        @Test(expected = IllegalArgumentException.class)
        public void ecoOntologyServiceWillNotProcessCoTermRequestsThrowsException() {

            String id = "GO:0003824";
            int limit = 5;
            float simThreshold = 0f;

            doThrow(new IllegalArgumentException()).when(coTermsRepositoryMock).findCoTerms(id, CoTermSource.MANUAL,
                    limit, simThreshold );

           ecoOntologyService.findCoTermsByOntologyId(id, CoTermSource.MANUAL, limit,
                    simThreshold );


        }
    }

    private static <ItemType> List<ItemType> asList(Collection<ItemType> items) {
        return items.stream().collect(Collectors.toList());
    }
}
