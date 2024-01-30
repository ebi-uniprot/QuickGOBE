package uk.ac.ebi.quickgo.ontology.common;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.quickgo.common.QueryUtils;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Test that the ontology repository can be accessed as expected.
 * <p>
 * Created 11/11/15
 *
 * @author Edd
 */

// temporary data store for solr's data, which is automatically cleaned on exit
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = OntologyRepoConfig.class)
class OntologyRepositoryIT {
    private static final String COLLECTION = SolrCollectionName.ONTOLOGY;

    @Autowired
    private OntologyRepository ontologyRepository;

    @Autowired
    private SolrTemplate ontologyTemplate;

    @BeforeEach
    void before() {
        ontologyRepository.deleteAll();
    }

    @Test
    void add1DocumentThenFind1Documents() throws IOException, SolrServerException {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findAll(PageRequest.of(0, 10)).getTotalElements(), is(1L));
    }

    @Test
    void add3DocumentsThenFind3Documents() {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));
        ontologyRepository.save(OntologyDocMocker.createGODoc("B", "Bob The Builder"));
        ontologyRepository.save(OntologyDocMocker.createGODoc("C", "Clint Eastwood"));

        assertThat(ontologyRepository.findAll(PageRequest.of(0, 10)).getTotalElements(), is(3L));
    }

    @Test
    void retrieves1DocCoreFieldsOnly() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> results =
                ontologyRepository.findCoreAttrByTermId(OntologyType.GO.name(), buildIdList(id));
        assertThat(results.size(), is(1));

        OntologyDocument ontologyDocument = results.get(0);
        assertThat(copyAsCoreDoc(ontologyDocument), is(equalTo(ontologyDocument)));
    }

    @Test
    void retrieves2DocsCoreFieldsOnly() {
        String id1 = "GO:0000001";
        String id2 = "GO:0000002";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id1, "GO name 1"));
        ontologyRepository.save(OntologyDocMocker.createGODoc(id2, "GO name 2"));
        ontologyRepository.save(OntologyDocMocker.createGODoc("GO:0000003", "GO name 3"));

        ontologyRepository.findAll().forEach(System.out::println);
        List<OntologyDocument> results =
                ontologyRepository.findCoreAttrByTermId(OntologyType.GO.name(), buildIdList(id1,id2));
        assertThat(results.size(), is(2));

        results.forEach(doc -> assertThat(copyAsCoreDoc(doc), is(equalTo(doc))));
    }

    @Test
    void retrievesReplacesField() {
        String id = "GO:0000001";
        OntologyDocument doc = OntologyDocMocker.createGODoc(id, "GO name 1");
        ontologyRepository.save(doc);

        List<OntologyDocument> resultList =
                ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument ontologyDocument = resultList.get(0);
        assertThat(ontologyDocument.replaces, hasSize(doc.replaces.size()));
    }

    @Test
    void retrievesReplacementField() {
        String id = "GO:0000001";
        OntologyDocument doc = OntologyDocMocker.createGODoc(id, "GO name 1");
        ontologyRepository.save(doc);

        List<OntologyDocument> resultList =
                ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument ontologyDocument = resultList.get(0);
        assertThat(ontologyDocument.replacements, hasSize(doc.replacements.size()));
    }

    @Test
    void retrievesHistoryField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository.findHistoryByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument doc = resultList.get(0);
        OntologyDocument docToMatch = copyAsBasicDoc(doc);
        docToMatch.history = doc.history;
        assertThat(doc, is(equalTo(docToMatch)));
    }

    @Test
    void retrievesXrefsField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository.findXRefsByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument doc = resultList.get(0);
        OntologyDocument docToMatch = copyAsBasicDoc(doc);
        docToMatch.xrefs = doc.xrefs;
        assertThat(doc, is(equalTo(docToMatch)));
    }

    @Test
    void retrievesAnnotationGuidelinesField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository
                        .findAnnotationGuidelinesByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument doc = resultList.get(0);
        OntologyDocument docToMatch = copyAsBasicDoc(doc);
        docToMatch.annotationGuidelines = doc.annotationGuidelines;
        assertThat(doc, is(equalTo(docToMatch)));
    }

    @Test
    void retrievesTaxonConstraintsField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository
                        .findTaxonConstraintsByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument doc = resultList.get(0);
        OntologyDocument docToMatch = copyAsBasicDoc(doc);
        docToMatch.taxonConstraints = doc.taxonConstraints;
        docToMatch.blacklist = doc.blacklist;
        assertThat(doc, is(equalTo(docToMatch)));
    }

    @Test
    void retrievesXOntologyRelationsField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository
                        .findXOntologyRelationsByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument doc = resultList.get(0);
        OntologyDocument docToMatch = copyAsBasicDoc(doc);
        docToMatch.xRelations = doc.xRelations;
        assertThat(doc, is(equalTo(docToMatch)));
    }

    @Test
    void add1DocumentAndFailToFindForWrongId() {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findCoreAttrByTermId(OntologyType.GO.name(), buildIdList("B")).size(), is(0));
        assertThat(ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList("B")).size(),
                is(0));
    }

    @Test
    void add1GoDocumentAndFindItById() {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findCoreAttrByTermId(OntologyType.GO.name(), buildIdList("A")).size(), is(1));
        assertThat(ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList("A")).size(), is(1));
        assertThat(ontologyRepository.findHistoryByTermId(OntologyType.GO.name(), buildIdList("A")).size(), is(1));
    }

    @Test
    void add1GoAnd1EcoDocumentsAndFindAllOfTypeGO() {
        OntologyDocument goDoc = OntologyDocMocker.createGODoc("A", "Alice Cooper");
        OntologyDocument ecoDoc = OntologyDocMocker.createECODoc("B", "Bob The Builder");

        ontologyRepository.save(goDoc);
        ontologyRepository.save(ecoDoc);

        Page<OntologyDocument> pagedDocs =
                ontologyRepository.findAllByOntologyType(OntologyType.GO.name(), PageRequest.of(0, 2));

        assertThat(pagedDocs.getTotalElements(), is(1L));
        assertThat(pagedDocs.getContent().get(0).getUniqueName(), is(goDoc.getUniqueName()));
    }

    @Test
    void add3GoDocumentsAndFindAllOfTypeGOWith1DocPerPage() {
        List<OntologyDocument> ontologyDocuments = Arrays.asList(
                OntologyDocMocker.createGODoc("A", "Alice Cooper"),
                OntologyDocMocker.createGODoc("B", "Bob The Builder"),
                OntologyDocMocker.createGODoc("C", "Clint Eastwood")
        );

        ontologyRepository.saveAll(ontologyDocuments);

        int count = 0;
        for (OntologyDocument ontologyDocument : ontologyDocuments) {
            Page<OntologyDocument> pagedDocs =
                    ontologyRepository.findAllByOntologyType(OntologyType.GO.name(), PageRequest.of(count++, 1));

            assertThat(pagedDocs.getContent(), hasSize(1));
            assertThat(pagedDocs.getContent().get(0).getUniqueName(), is(ontologyDocument.getUniqueName()));
        }
    }

    /**
     * Shows how to save directly to a solr server, bypassing transactional
     * operations that are managed by Spring.
     *
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    void saveDirectlyToSolrServer() throws IOException, SolrServerException {
        ontologyTemplate.getSolrClient().addBean(COLLECTION,OntologyDocMocker.createGODoc("A", "Alice Cooper"));
        ontologyTemplate.getSolrClient().addBean(COLLECTION,OntologyDocMocker.createGODoc("B", "Alice Cooper"));
        ontologyTemplate.getSolrClient().addBean(COLLECTION,OntologyDocMocker.createGODoc("C", "Alice Cooper"));
        ontologyTemplate.getSolrClient().addBeans(COLLECTION,
                Arrays.asList(
                        OntologyDocMocker.createGODoc("D", "Alice Cooper"),
                        OntologyDocMocker.createGODoc("E", "Alice Cooper")));

        assertThat(ontologyRepository.findAll(PageRequest.of(0, 10)).getTotalElements(), is(0L));

        ontologyTemplate.getSolrClient().commit(COLLECTION);

        assertThat(ontologyRepository.findAll(PageRequest.of(0, 10)).getTotalElements(), is(5L));
    }

    @Test
    void retrievesSecondaryIdByPrimary(){
        retrievesSecondaryIdField(buildIdList("GO:0000001"));
    }

    @Test
    void retrievesSecondaryIdBySecondary(){
        retrievesSecondaryIdField(buildIdList("GO:0000003"));
    }

    @Test
    void retrievesSecondaryIdMix(){
        retrievesSecondaryIdField(buildIdList("GO:0000001", "GO:0000003", "GO:0000004"));
    }

    private void retrievesSecondaryIdField(List<String> searchIds) {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        var resultList = ontologyRepository.findSecondaryIdsByTermId(OntologyType.GO.name(), searchIds);

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument doc = resultList.get(0);
        OntologyDocument docToMatch = copyAsBasicDoc(doc);
        docToMatch.secondaryIds = doc.secondaryIds;
        assertThat(doc, is(equalTo(docToMatch)));
    }

    private OntologyDocument copyAsBasicDoc(OntologyDocument document) {
        OntologyDocument basicDoc = new OntologyDocument();
        basicDoc.id = document.id;
        basicDoc.isObsolete = document.isObsolete;
        basicDoc.name = document.name;
        basicDoc.comment = document.comment;
        basicDoc.definition = document.definition;
        return basicDoc;
    }

    private OntologyDocument copyAsCoreDoc(OntologyDocument document) {
        OntologyDocument coreDoc = copyAsBasicDoc(document);
        coreDoc.usage = document.usage;
        coreDoc.synonyms = document.synonyms;
        coreDoc.aspect = document.aspect;
        return coreDoc;
    }

    private List<String> buildIdList(String... ids) {
        return Arrays.stream(ids)
                .map(QueryUtils::solrEscape)
                .collect(Collectors.toList());
    }
}